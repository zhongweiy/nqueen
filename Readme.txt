** The implementation of the following problem:
   Place N queens on an NxN chess board so that none of them attack each other (the classic n-queens problem). Additionally, please make sure that no three queens are in a straight line at ANY angle, so queens on A1, C2 and E3, despite not attacking each other, form a straight line at some angle.

** Run it with after build:
  // n = 15
  $java -jar nqueen-1.0-SNAPSHOT.jar 15

** Using multi-thread to do performance improvement
   - first version (commit 3a8cc27)
       N=15, Solution size=18752, Time (seconds):13.554083606

   - using multi-thread (thread number is 4)
       N=15, Solution size=18752, Time (seconds):7.068219659
     4 threads does not provide 4 times better performance here due to my test host is 2 cores CPU with hyper-threading. Since this task is computational intensive, we will not benifit from hyper-threading here.

** This biggest number tried is N = 17. Here is the result:
   There are 235056 solutions.
   The first 10 Solutions are: [[0, 2, 5, 10, 6, 9, 14, 1, 3, 15, 8, 16, 4, 12, 7, 11, 13], [0, 2, 5, 11, 14, 4, 10, 13, 15, 3, 7, 16, 1, 9, 12, 6, 8], [0, 2, 5, 12, 15, 7, 10, 6, 1, 14, 16, 3, 9, 4, 8, 13, 11], [0, 2, 5, 12, 15, 7, 10, 6, 14, 1, 4, 16, 8, 11, 9, 3, 13], [0, 2, 5, 15, 6, 12, 10, 1, 4, 14, 16, 8, 3, 11, 13, 7, 9], [0, 2, 5, 16, 9, 12, 14, 3, 7, 13, 8, 1, 4, 15, 11, 6, 10], [0, 2, 6, 1, 13, 7, 14, 12, 5, 16, 4, 10, 15, 3, 9, 11, 8], [0, 2, 6, 11, 9, 14, 4, 13, 7, 16, 12, 5, 15, 8, 10, 3, 1], [0, 2, 6, 11, 13, 1, 5, 12, 15, 4, 8, 14, 9, 3, 16, 7, 10], [0, 2, 6, 13, 9, 16, 3, 10, 14, 11, 4, 1, 7, 5, 12, 8, 15]]
   Solution(#1):[0, 2, 5, 11, 14, 4, 10, 13, 15, 3, 7, 16, 1, 9, 12, 6, 8]
|*, , , , , , , , , , , , , , , , ,|
| , ,*, , , , , , , , , , , , , , ,|
| , , , , ,*, , , , , , , , , , , ,|
| , , , , , , , , , , ,*, , , , , ,|
| , , , , , , , , , , , , , ,*, , ,|
| , , , ,*, , , , , , , , , , , , ,|
| , , , , , , , , , ,*, , , , , , ,|
| , , , , , , , , , , , , ,*, , , ,|
| , , , , , , , , , , , , , , ,*, ,|
| , , ,*, , , , , , , , , , , , , ,|
| , , , , , , ,*, , , , , , , , , ,|
| , , , , , , , , , , , , , , , ,*,|
| ,*, , , , , , , , , , , , , , , ,|
| , , , , , , , , ,*, , , , , , , ,|
| , , , , , , , , , , , ,*, , , , ,|
| , , , , , ,*, , , , , , , , , , ,|
| , , , , , , , ,*, , , , , , , , ,|
   Runtime (seconds):226.884428885 with 4 threads
