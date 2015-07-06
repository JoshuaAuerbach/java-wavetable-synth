DEFINES        = -DDEBUG
#SYSDEFINE     = -DSYSTEM64BIT
SYSDEFINE      = -DSYSTEM32BIT
# define this for older kernels
#SYSDEFINE    += -DKERNEL_VER24
CFLAGS         = -Wall -O2
# paths for libc headers and ALSA headers, alsa/asoundlib.h et al
INCLUDES       = -I. -I/usr/include -I/usr/src/linux/include

# lib path for ALSA libasound.so
LDFLAGS        = -L/lib
LIBS           = -lasound
EXE1           = midiloop
MODULES1       = midiloop.o
CC             = gcc

.SUFFIXES:
.SUFFIXES: .c .o

%.o: %.c Makefile
	$(CC) $(CFLAGS) $(DEFINES) $(SYSDEFINE) $(INCLUDES) -c -o $@ $<

all: $(EXE1)

$(EXE1): $(MODULES1)
	$(CC) $(CFLAGS) $(MODULES1) $(LDFLAGS) $(LIBS) -o $@

clean:
	rm -f *.o *~
