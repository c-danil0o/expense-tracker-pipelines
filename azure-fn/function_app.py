import azure.functions as func
import logging
import requests

app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)

@app.timer_trigger(schedule="0 */10 * * * *", arg_name="myTimer", run_on_startup=True,
              use_monitor=False) 
def timer_trigger(myTimer: func.TimerRequest) -> None:
    
    if myTimer.past_due:
        logging.info('The timer is past due!')

    logging.info('Python timer trigger function executed.')
    url = "http://localhost:8080/api/notify"
    headers = {
            'Content-Type': 'application/json',
        }



    response = requests.get(url, headers=headers)

    if response.status_code == 200:
        print("Successfully sent notify request!")
    else:
        print(f"Failed to send notify request. Status Code: {response.status_code}")
    print(response.content)