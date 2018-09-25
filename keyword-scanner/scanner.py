import sys
import requests

###
# Install pexpect:
#   sudo apt install python-pip
#   sudo pip install requests
###

if len(sys.argv) != 4:
    print "usage: python mal-scanner.py [host] [port] [password-file]"
    exit(1)

HOST = sys.argv[1]
PORT = sys.argv[2]
FILE = sys.argv[3]

with open(FILE) as f:
    for line in f:
        credentials = line.split()
        username    = credentials[0]
        password    = credentials[1]
        if password == '(none)':
            password = ''

        print "auth credentials " + username + ":" + password
	url = "http://" + HOST +":" + PORT + "?a=YWZmaWQ9MDUyODg"
	print "request url: " + url
        r = requests.get(url)
        print r.status_code