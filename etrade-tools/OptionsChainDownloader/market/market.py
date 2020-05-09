import json
import logging
from logging.handlers import RotatingFileHandler

# logger settings
logger = logging.getLogger('my_logger')
logger.setLevel(logging.DEBUG)
handler = RotatingFileHandler("python_client.log", maxBytes=5 * 1024 * 1024, backupCount=3)
FORMAT = "%(asctime)-15s %(message)s"
fmt = logging.Formatter(FORMAT, datefmt='%m/%d/%Y %I:%M:%S %p')
handler.setFormatter(fmt)
logger.addHandler(handler)

class Market:
    def __init__(self, session, base_url):
        self.session = session
        self.base_url = base_url

    def chains(self, symbol):
        
        logger.debug("Retrieve chains for symbol = %s", symbol)
        
        url = self.base_url + "/v1/market/optionexpiredate?symbol=" + symbol
        logger.debug(url)
        response = self.session.get(url)
        
        if response is not None and response.status_code == 200:

            parsed = json.loads(response.text)
            logger.debug("Response Body: %s", json.dumps(parsed, indent=4, sort_keys=True))
            
            return response.json()
        
        else:
            logger.debug("Response Body: %s", response)
            raise Exception("Error: Quote API service error")
        
    def quotes(self, symbols):
        """
        Calls quotes API to provide quote details for equities, options, and mutual funds
        :param self: Passes authenticated session in parameter
        """
        # URL for the API endpoint
        url = self.base_url + "/v1/market/quote/" + symbols + ".json"
        logger.debug(url)

        # Make API call for GET request
        response = self.session.get(url)
        logger.debug("Request Header: %s", response.request.headers)

        if response is not None and response.status_code == 200:

            parsed = json.loads(response.text)
            logger.debug("Response Body: %s", json.dumps(parsed, indent=4, sort_keys=True))

            # Handle and parse response
            
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
            logger.debug("Response Body: %s", response)
            raise Exception("Error: Quote API service error")

        