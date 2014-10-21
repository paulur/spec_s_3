package com.spec.util;

public interface CONSTANT {
	String DATA_DIR			= "data_source\\";
	String USER_DATA_DIR 	= DATA_DIR + "userData\\";
	String TEST_DATA_DIR	= DATA_DIR + "test\\";
	String OUTPUT_COMBINE_DIR	= DATA_DIR + "output\\combine\\";
	String OUTPUT_GRAPH_DIR 	= DATA_DIR + "output\\graph\\";
	String OUTPUT_SEQUENCE_DIR 	= DATA_DIR + "output\\sequence\\";
	
	String EXPERIMENT 			= DATA_DIR + "Experiment\\";
	String TRAINING_DATA_DIR	= EXPERIMENT + "trainingData\\";
	String TESTING_DATA_DIR		= EXPERIMENT + "testingData\\";

	String TRAINING_ATTR_HEALTHY 	= EXPERIMENT + "TrainingAttr-healthy";
	String TRAINING_ATTR_FAILURE 	= EXPERIMENT + "TrainingAttr-failures";
	String TESTING_ATTR 			= EXPERIMENT + "testingAttr";
	
	String TRAINING_ATTR_All = EXPERIMENT + "IDSeq\\trainingAll-IDSeq.txt";
	String TESTCASE = EXPERIMENT + "IDSeq\\testcase.txt"; 

	String USER_LOG_POST_FIX	= ".log";
	String HEALTHY_POST_FIX		= "";
	String TESTING_POST_FIX		= ".log";
	String FAILRE_POST_FIX		= ".txt";
	
	
	int NOT_SIGNED_ID		= -1;
	int DOUBLE_VALUE_PREC	= 6;
	
	String EVENT_ENTER = "enter";
	String EVENT_LEAVE = "leave";
	String EVENT_CLICK = "click";
	
	int CODE_ENTER = 1;
	int CODE_CLICK = 2;
	int CODE_FOCUS = 3;

	String HTTP_TYPE	= "HTTP";
	String INPUT_TYPE	= "INPUT";
	String IMAGE_TYPE 	= "IMAGE";
	
	String IMAGE		= "IMAGE";
	String image		= "image";
	String IMG			= "IMG";
	String img			= "img";
	
	String TEXTAREA		= "TEXTAREA";
	String TEXTFIELD	= "|INPUT|text";
	
	double threshold	= 0.005;
	double SIMILAR_THRESHOLD = 0.90;
	int KNOW_FAILURE	= 1;
	int KNOW_PASS		= 0;
	int KNOW_TEST		= -1;
	
	double epsilon = 0.00001;
	
}
