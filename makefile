JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
	echo Main-Class: PartitionMethod > MANIFEST.MF
	jar -cvmf MANIFEST.MF PartitionMethod.jar PartitionMethod.class

CLASSES = \
	PartitionMethod.java
default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
	$(RM) *.log
	$(RM) *.aux
	$(RM) *.MF
