SRCDIR=src
BINDIR=bin

SRCPAT=$(SRCDIR)/%.java
BINPAT=$(BINDIR)/$(SRCDIR)/%.class

SRCS=$(shell find $(SRCDIR) -name *.java -type f)
BINS=$(SRCS:$(SRCPAT)=$(BINPAT))

JFLAGS=

ENTRY=Lox
ARGS=../script.lox

.PHONY: run

all: $(BINDIR) $(BINS)

$(BINPAT): $(SRCPAT)
	javac $(JFLAGS) -d $(BINDIR) $<

$(BINDIR):
	mkdir -p $(BINDIR)

run:
	java -cp $(BINDIR) $(SRCDIR).$(ENTRY) $(ARGS)

clean:
	rm -rf $(BINDIR)

