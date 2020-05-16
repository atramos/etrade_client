import json

class Market:
    def __init__(self, session, base_url):
        self.session = session
        self.base_url = base_url

    def chains(self, symbol, params):
        
        url = self.base_url + "/v1/market/optionchains.json"
        params['overrideSymbolCount'] = 'true'
        params['symbol'] = symbol
        response = self.session.get(url, params=params)
        
        if response is not None and response.status_code == 200:
            parsed = json.loads(response.text)
            return response.json()
        
        else:
            raise Exception("Error: Quote API service error")

    def chainDates(self, symbol):
        
        url = self.base_url + "/v1/market/optionexpiredate.json"
        params = {"overrideSymbolCount": "true", "symbol": symbol}
        response = self.session.get(url, params=params)
        
        if response is not None and response.status_code == 200:
            parsed = json.loads(response.text)
            return response.json()['OptionExpireDateResponse']['ExpirationDate']
        
        else:
            raise Exception("Error: Quote API service error")
        
    def quotes(self, symbols):
        """
        Calls quotes API to provide quote details for equities, options, and mutual funds
        :param self: Passes authenticated session in parameter
        """
        # URL for the API endpoint
        url = self.base_url + "/v1/market/quote/" + symbols + ".json"

        # Make API call for GET request
        response = self.session.get(url)

        if response is not None and response.status_code == 200:
            parsed = json.loads(response.text)
            data = response.json()
            if data is not None and "QuoteResponse" in data and "QuoteData" in data["QuoteResponse"]:
                return data["QuoteResponse"]["QuoteData"]
            else:
                # Handle errors
                if data is not None and 'QuoteResponse' in data and 'Messages' in data["QuoteResponse"] \
                        and 'Message' in data["QuoteResponse"]["Messages"] \
                        and data["QuoteResponse"]["Messages"]["Message"] is not None:
                    errors = ["Error: " + error_message["description"] for error_message in data["QuoteResponse"]["Messages"]["Message"]]
                    raise Exception('\n'.join(errors))
                else:
                    raise Exception("Error: Quote API service error")
        else:
            raise Exception("Error: Quote API service error")

        