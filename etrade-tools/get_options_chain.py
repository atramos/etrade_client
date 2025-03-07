"""This Python script provides examples on using the E*TRADE API endpoints"""
from __future__ import print_function
import webbrowser
import json
import logging
import configparser
import sys
import requests
from rauth import OAuth1Service
from logging.handlers import RotatingFileHandler
from market.market import Market

# logger settings
logger = logging.getLogger('my_logger')
logger.setLevel(logging.DEBUG)
handler = RotatingFileHandler("python_client.log", maxBytes=5*1024*1024, backupCount=3)
FORMAT = "%(asctime)-15s %(message)s"
fmt = logging.Formatter(FORMAT, datefmt='%m/%d/%Y %I:%M:%S %p')
handler.setFormatter(fmt)
logger.addHandler(handler)

def oauth():
    """Allows user authorization for the sample application with OAuth 1"""
    etrade = OAuth1Service(
        name="etrade",
        consumer_key=config["DEFAULT"]["CONSUMER_KEY"],
        consumer_secret=config["DEFAULT"]["CONSUMER_SECRET"],
        request_token_url="https://api.etrade.com/oauth/request_token",
        access_token_url="https://api.etrade.com/oauth/access_token",
        authorize_url="https://us.etrade.com/e/t/etws/authorize?key={}&token={}",
        base_url="https://api.etrade.com")

    base_url = config["DEFAULT"]["PROD_BASE_URL"]

    # Step 1: Get OAuth 1 request token and secret
    request_token, request_token_secret = etrade.get_request_token(
        params={"oauth_callback": "oob", "format": "json"})

    # Step 2: Go through the authentication flow. Login to E*TRADE.
    # After you login, the page will provide a text code to enter.
    authorize_url = etrade.authorize_url.format(etrade.consumer_key, request_token)
    webbrowser.open(authorize_url)
    text_code = input("Please accept agreement and enter text code from browser: ")

    # Step 3: Exchange the authorized request token for an authenticated OAuth 1 session
    session = etrade.get_auth_session(request_token,
                                  request_token_secret,
                                  params={"oauth_verifier": text_code})

    return(session, base_url)


# loading configuration file
config = configparser.ConfigParser()
config.read(sys.argv[1])
(session, base_url) = oauth()
market = Market(session, base_url)
quotes = market.quotes(sys.argv[2])

for stock in quotes:
    symbol = stock['Product']['symbol']
    
    chainDates = market.chainDates(symbol)
    
    chains = [market.chains(symbol, {
            'expiryYear': chainDate['year'],
            'expiryMonth': chainDate['month'],
            'expiryDay': chainDate['day'],
            'includeWeekly': 'true',
            'priceType': 'ALL'
        }) for chainDate in chainDates]
        
    data = { 'stock': stock, 'options': chains, 'optionsDates': chainDates }
    
    with open("sample_data/" + symbol + ".json", "a") as f:
        f.write(json.dumps(data, indent=4))
        print('Wrote: ' + symbol)
        