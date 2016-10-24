SRCDIR = src
BINDIR = bin

JAVAC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR):$(JUNIT)

vpath %.java $(SRCDIR)
vpath %.class $(BINDIR)

# define general build rule for java sources
.SUFFIXES:  .java  .class

.java.class:
	$(JAVAC)  $(JFLAGS)  $<

# default rule - will be invoked by make

# Wow, this code seems like a mess, need to compile everything together...
all: 
	javac $(JFLAGS) $(SRCDIR)/Book.java $(SRCDIR)/BookGUI.java $(SRCDIR)/FullBookGUI.java \
	$(SRCDIR)/Library.java $(SRCDIR)/LibraryGUI.java $(SRCDIR)/FileManager.java

run: all
	java -cp $(BINDIR) LibraryGUI

clean:
	@rm -f $(BINDIR)/*.class