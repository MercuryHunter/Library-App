SRCDIR = src
BINDIR = bin

JAVAC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR):$(JUNIT)

vpath %.java $(SRCDIR)/gui:$(SRCDIR)/main:$(SRCDIR)/util
vpath %.class $(BINDIR)/gui:$(BINDIR)/main:$(BINDIR)/util

# define general build rule for java sources
.SUFFIXES:  .java  .class

.java.class:
	$(JAVAC)  $(JFLAGS)  $<

# default rule - will be invoked by make

# Wow, this code seems like a mess, need to compile everything together...
all: 
	javac $(JFLAGS) $(SRCDIR)/main/Book.java $(SRCDIR)/main/Library.java \
	$(SRCDIR)/gui/BookGUI.java $(SRCDIR)/gui/FullBookGUI.java $(SRCDIR)/gui/LibraryGUI.java $(SRCDIR)/gui/HintTextField.java \
	$(SRCDIR)/util/FileManager.java

run: all
	java -cp $(BINDIR) gui.LibraryGUI

clean:
	@rm -f $(BINDIR)/*.class
	@rm -f $(BINDIR)/*/*.class
	@rm -rf books
	@rm -rf images
	@rm -f masterlist.txt deleted.txt
