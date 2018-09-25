# This script traverse the currnt directory to
# 1. search keywords in files
# 2. search key types of file
# Returned search results are printed to terminal and written to the log file.

import os, sys, datetime

FileTypes = ['properties']
SearchStrings = ['keystore', 'password', 'credential', 'secret']
KeyFileTypes = ['.p12', '.jks']
LogFileName = "Scan_LOG.txt"
EventTypes = {
                "keyword" : "KEYWORD",
                "file" : "KEYFILE"
            }

discovered_keywords = list()
discovered_files = list()

def scan(top, target_file_types, target_keywords, target_files):
    for root, dirs, files in os.walk(top, topdown=False):
        for fl in files:
            current_file = os.path.join(root, fl)
            for kf_type in target_files:
                status = str.endswith(current_file, kf_type)
                if str(status) == 'True':
                    print(current_file + '\n')
                    discovered_files.append(current_file)
            for f_type in target_file_types:
                status = str.endswith(current_file, f_type)
                if str(status) == 'True':
                    for word in target_keywords:
                        search_keyword(word, current_file)

def search_keyword(keyword, file):
    with open(file) as f:
        for line in f:
            if keyword in line:
                entry = line + "\t" + file
                print entry
                discovered_keywords.append(entry)

def create_log_header(_log_file):
    _log_file.write("Scanner processed against directory \"" + os.getcwd() + "\".\n==Started @:" + str(
        datetime.datetime.now()) + "==\n")

def create_log(_log_file):
    _log_file.write("\n--discovered keywords--\n")
    write_list_log(_log_file, discovered_keywords, EventTypes["keyword"])
    # _log_file.write("\n--discovered files--\n")
    # write_list_log(_log_file, discovered_files, EventTypes["file"])

def create_log_footer(_log_file):
    _log_file.write("==Scanning Result==\nCompleted @:" + str(datetime.datetime.now()) + "\n")
    _log_file.write("%d keywords and %d files are discovered." % (len(discovered_keywords), len(discovered_files)))

def write_list_log(file, list, event_type):
    for l in list:
        file.write("%s: %s\n" % (event_type, l))

def main():
    if (len(sys.argv) != 1 and len(sys.argv)) != 2:
        print "usage: python keywords-scanner.py {scanning-dir}"
        exit(1)

    RootOutput = os.getcwd()
    log_file = open(RootOutput + '\\' + LogFileName, 'w')

    create_log_header(log_file)

    if (len(sys.argv) == 2):
        RootOutput = sys.argv[1]
    scan(RootOutput, FileTypes, SearchStrings, KeyFileTypes)

    create_log(log_file)
    create_log_footer(log_file)

    log_file.close()

    print ("%d files and %d keywords are discovered." % (len(discovered_keywords), len(discovered_files)))


if __name__ == '__main__':
    main()

