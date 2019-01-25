import kubernetes
import time
import os
import sys
import urllib.request
import json
import pprint
import requests
from subprocess import call

def Scale():
    currentRelativeReplicaCount = 1
    adjustReplicaCount(1)
    while True:
        msgCount = poll()
        print(msgCount)
        oldCount = currentRelativeReplicaCount
        if int(msgCount) > currentRelativeReplicaCount:
            currentRelativeReplicaCount = currentRelativeReplicaCount + 1
            if currentRelativeReplicaCount >= 3:
                currentRelativeReplicaCount = 3
        if int(msgCount) <= currentRelativeReplicaCount:
            currentRelativeReplicaCount = currentRelativeReplicaCount -1
        if currentRelativeReplicaCount == 0:
            currentRelativeReplicaCount = 1
        print(msgCount)
        print(currentRelativeReplicaCount)
        # currentRelativeReplicaCount = 2
        if(oldCount != currentRelativeReplicaCount):
            adjustReplicaCount(currentRelativeReplicaCount)
        time.sleep(4)

def poll():
    #contents = urllib.request.urlopen("134.103.195.110:9090/api/v1/query/query=rabbitmq_queue_messages")
    url = "http://134.103.195.110:9090/api/v1/query"
    payload = {'query': 'rabbitmq_queue_messages'}
    contents = requests.get(url, params=payload)
    print(contents.text)

    rjson = json.loads(contents.text)
    i = 0
    while i < 10000:
        print(rjson['data']['result'][i]['metric']['queue'])
        if rjson['data']['result'][i]['metric']['queue'] == "rpc_queue":
            return rjson['data']['result'][i]['value'][1]
        i = i+1
    return

def adjustReplicaCount(newCount):
    if newCount == 0:
        return
    command = "kubectl patch deployment appcontainer-deployment -p '{\"spec\":{\"replicas\":"+str(newCount)+"}}'"
    print(command)
    os.system(command)
    return

if __name__ == "__main__":
    Scale()
