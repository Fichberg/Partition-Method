JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	PartitionMethod.java \
	Partition.java
default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
	$(RM) *.log
	$(RM) *.aux
