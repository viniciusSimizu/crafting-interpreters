SRCDIR=src
BINDIR=bin

SRCPAT=$(SRCDIR)/%.java
BINPAT=$(BINDIR)/$(SRCDIR)/%.class

SRCS=$(shell find $(SRCDIR) -name *.java -type f)
BINS=$(SRCS:$(SRCPAT)=$(BINPAT))

ENTRY=Lox
ARGS=

.PHONY: run

all: $(BINDIR) $(BINS)

$(BINPAT): $(SRCPAT)
	javac -d $(BINDIR) $<

$(BINDIR):
	mkdir -p $(BINDIR)

run:
	@java -cp $(BINDIR) $(SRCDIR).$(ENTRY) $(ARGS)

clean:
	rm -rf $(BINDIR)

