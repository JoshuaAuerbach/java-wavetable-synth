# root of the installation
BASE_PATH      = ../../..
# root path of the JDK
JDK            := /usr/lib/java
JAVASRC        = ..
#DEFINES        = -DDEBUG
#SYSDEFINE     = -DSYSTEM64BIT
SYSDEFINE      = -DSYSTEM32BIT
# define this for older kernels
#SYSDEFINE    += -DKERNEL_VER24
CFLAGS         = -Wall -O2 -fPIC
# paths for libc headers and ALSA headers, alsa/asoundlib.h et al
INCLUDES       = -I. -I$(JDK)/include -I$(JDK)/include/linux -I/usr/include -I/usr/src/linux/include

# lib path for ALSA libasound.so
LDFLAGS        = -L/lib
#-L$(BASE_PATH)/lib/native
LIBS           = -lasound
LIB1           = directaudiosink
FULLLIB1       = lib$(LIB1).so
LIB2           = directmidiin
FULLLIB2       = lib$(LIB2).so
JNIHEADERS     = directalsa.h directmidiin.h
MODULES1       = directalsa.o
MODULES2       = directmidiin.o
CC             = gcc
JAVAH          = $(JDK)/bin/javah
JAVAC          = $(JDK)/bin/javac
CP             = $(JAVASRC):../synth.jar:../tuningForkTraceGeneration.jar
#:../xlib/xrts.jar
CURRDIR       := `pwd`

.SUFFIXES:
.SUFFIXES: .c .o

%.o: %.c Makefile
	$(CC) $(CFLAGS) $(DEFINES) $(SYSDEFINE) $(INCLUDES) -c -o $@ $<

%.class: %.java
	$(JAVAC) -classpath $(CP) $<

all: $(FULLLIB1) $(FULLLIB2)

$(FULLLIB1): $(JNIHEADERS) $(MODULES1)
	$(CC) $(CFLAGS) $(MODULES1) $(LDFLAGS) $(LIBS) -shared -Wl,-soname,lib$(LIB).so -o $@

$(FULLLIB2): $(JNIHEADERS) $(MODULES2)
	$(CC) $(CFLAGS) $(MODULES2) $(LDFLAGS) $(LIBS) -shared -Wl,-soname,lib$(LIB).so -o $@

directalsa.h: $(JAVASRC)/com/ibm/realtime/synth/modules/DirectAudioSink.class
	$(JAVAH) -classpath $(CP) -o $@ com.ibm.realtime.synth.modules.DirectAudioSink

directmidiin.h: $(JAVASRC)/com/ibm/realtime/synth/modules/DirectMidiIn.class
	$(JAVAH) -classpath $(CP) -o $@ com.ibm.realtime.synth.modules.DirectMidiIn

clean:
	rm -f *.o *~ $(JNIHEADERS)
