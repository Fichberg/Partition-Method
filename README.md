Usage:
	java PartitionMethod <input_file> [-g]

Note: <input_file> must be inside inputs directory.
      -g is an optional paramater for step-by-step execution.


About the input file format:

first line: total number of states
second line: total number of possible next states for each state, considering each input.
third line: total number of outputs for each state, considering each input.
Remaining lines:
	First column: input (R stands for Reset) 
	Second Column: current state 
	Third Column: List of next states for the current state considering the input. States are ordered for crescent inputs (0, 1, 10, 11...)
	Fourth Column: List of outputs for the current state considering the input. Outputs are ordered for crescent inputs, i.e, (0 1 1) means output 
           0 for input 0, output 1 for input 1, output 1 for input 10 and so on.
