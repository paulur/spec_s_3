# This script traverse the currnt directory to
# 1. search keywords in files 
# 2. search key types of file 
# Returend search results are printed to terminal and written to the log file.

import os, sys, datetime

def scan(top, file_types, keywords, keyfiles, log_file):
	for root, dirs, files in os.walk(top, topdown=False):
		for fl in files:
			currentFile=os.path.join(root, fl)
			for kf_type in keyfiles:
				status= str.endswith(currentFile, kf_type)
				if str(status) == 'True':
					log('KEYFILES', currentFile + '\n', log_file)
			for f_type in file_types:
				status= str.endswith(currentFile, f_type)
				if str(status) == 'True':
					for word in keywords:
						search_keyword(word, currentFile, log_file)


def search_keyword(keyword, file, log_file):
	with open(file) as f:
		for line in f:
			if keyword in line:
				log('KEYWORDS', file+"\n\t"+line, log_file)
	

def log(eventType, event, logFile):
	entry = eventType + ":" + event
	logFile.write(entry)
	print entry
	
def main():
	if len(sys.argv) != 1:
		print "usage: python keywords-scanner.py"
		exit(1)

	RootOutput = os.getcwd()
	FileTypes=['properties']
	SearchStrings=['keystore', 'password']
	KeyFileTypes=['.p12']
	LogFileName="Scan_LOG.txt"

	print "Working in: "+os.getcwd()
	
	log_file = open(RootOutput+'\\' + LogFileName, 'w')
	log_file.write("Log of files Succesfully processed. RESULT of process run @:"+str(datetime.datetime.now())+"\n")
	
	scan(RootOutput, FileTypes, SearchStrings, KeyFileTypes, log_file)
	
	log_file.close()

	
		
# this is the standard boilerplate that calls the main() function
if __name__ == '__main__':
    # sys.exit(main(sys.argv)) # used to give a better look to exists
    main()
	
