JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:/bin/javac::")
JNI_DIR = "$(JAVA_HOME)/include"

# Check OS
UNAME := $(shell uname)

ifeq ($(UNAME), Linux)
	JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:/bin/javac::")
	TARGET = libshotgun.so
	JNI_DIR_OS = "$(JNI_DIR)/linux"
else
	ifeq ($(UNAME), Darwin)
		JAVA_HOME = $(shell /usr/libexec/java_home)
		TARGET = libshotgun.jnilib
		JNI_DIR_OS = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"
	else
		JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:/bin/javac::")
		TARGET = shotgun.dll
		JNI_DIR_OS = "$(JNI_DIR)/win32"
	endif
endif
	
$(TARGET): shotgun.cpp common.h logreg.cpp shared.cpp
	g++ -I $(JNI_DIR) -I $(JNI_DIR_OS) -fopenmp -pthread -shared \
shotgun.cpp lasso.cpp mmio.c logreg.cpp write_matrix_market.cpp \
shared.cpp -o $@ -fPIC -lgomp -O3
