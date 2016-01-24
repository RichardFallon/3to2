# 3to2
Conversion of Conceptual Graphs (in CGIF file format) into Formal Concepts (in Burmeister file format) and back again for round-trip engineering.

This project uses ideas and source code (in C) taken from the Sourceforge project cgfca (http://sourceforge.net/projects/cgfca/) by 
Dr. Simon Andrews, Sheffield Hallam University (https://www.shu.ac.uk/research/c3ri/people/dr-simon-andrews) together with
ideas from the paper "A Mapping from Conceptual Graphs to Formal Concept Analysis".

From this basis the application has been converted into JAVA and extended to allow for conversion first from Conceptual
Graphs into Formal Concepts for Formal Concept Analysis and then for conversion back from Formal Concepts into 
Conceptual Graphs thus allowing for round-trip engineering. 

The file formats used are;
	Conceptual Graphs in CGIF (http://www.jfsowa.com/cg/annexb.htm) format (CGIF)
	Formal Concepts in Burmeister file format (CXT)
	
Example CXT and CGIF files are provided in the example folder.

To run the application; JAVA -JAR 3to2.JAR

Andrews, Simon; Polovina, Simon (2011) "A Mapping from Conceptual Graphs to Formal Concept Analysis". 
In: Conceptual Structures for Discovering Knowledge (The 19th International Conference on Conceptual Structures, ICCS 2011, Derby, UK) 
Andrews, Simon; Polovina, Simon; Hill, Richard; Akhgar, Babak (Eds.), 
Lecture Notes in Computer Science, Vol. 6828 (Subseries: Lecture Notes in Artificial Intelligence), Springer, 63-76.