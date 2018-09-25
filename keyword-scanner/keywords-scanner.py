# Import the os module, for the os.walk function
import os
import sys

def search(file, keyword):
	print ('search file "%s" for keyword "%s"' % (file, keyword))
	with open(file) as f:
		for line in f:
			if keyword in line:
				print('keyword "%s" found in line "%s"' %(keyword, line))


def main():
	if len(sys.argv) != 4:
		print "usage: python keywords-scanner.py [REPO_DIR] [TARGET_POSTFIX] [KEYWORD]"
		exit(1)

	REPO_DIR = sys.argv[1]
	TARGET_POSTFIX = sys.argv[2]
	KEYWORD = sys.argv[3]
	print("\ntraverse file dir: " + REPO_DIR)
	for dirName, subdirList, fileList in os.walk(REPO_DIR):
		for fname in fileList:
			if fname.endswith(TARGET_POSTFIX):
				fileFull = REPO_DIR + subdirList 
				fileFull += fname
				print('\t%s' % (fileFull)) 
    
# this is the standard boilerplate that calls the main() function
if __name__ == '__main__':
    # sys.exit(main(sys.argv)) # used to give a better look to exists
    main()