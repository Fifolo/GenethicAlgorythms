package com.starczi;

import java.io.*;
import java.util.*;

public class Main {

    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {

        //creaton of arrays with x and y coordinates
        String bier127 = "bier127.txt";
        String pr144 = "pr144.txt";

        ArrayList<ArrayList> XiY = fileReading(pr144);

        long[] x = new long[XiY.get(0).size()];
        long[] y = new long[XiY.get(1).size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = (long) (XiY.get(0).get(i));
            y[i] = (long) (XiY.get(1).get(i));
        }
        long[][] matrixOfDistances = new long[x.length][y.length];
        makeMatrixOfDistances(matrixOfDistances, x, y);

        PrintWriter save = new PrintWriter("wyniki.txt");
        int shortestIndex;
        for (int j = 0; j < 5; j++) {
            int[][] population = new int[144][144];
            randomizePopulation(population, x.length);
            System.out.println("Najkrotsza odlegosc -> " + getShortestDistance(population, matrixOfDistances));

            System.out.println("\n\t******algorytm******\n");

            final long NANOSEC_PER_SEC = 1000l * 1000 * 1000;

            long startTime = System.nanoTime();

            while ((System.nanoTime() - startTime) < 0.1 * 60 * NANOSEC_PER_SEC) {
                population = tournamentSelection(population, matrixOfDistances);
                inversionMutation(population, 0.4);
                population = OrderOneCrossover(population, 0.2);
            }

            shortestIndex = getShortestIndex(population, matrixOfDistances);
            System.out.println("Shortest index -> " + shortestIndex);
            System.out.println("Shortest distance -> " + getDistatnce(population[shortestIndex], matrixOfDistances));
            System.out.println("\n\t===Next population===\n");
            save.print(getDistatnce(population[shortestIndex], matrixOfDistances));
            save.println();
            for (int i = 0; i < population[shortestIndex].length; i++) {
                if (i == population[shortestIndex].length - 1) {
                    save.print(population[shortestIndex][i] + "\n");
                } else
                    save.print(population[shortestIndex][i] + "-");
            }
            save.println();
        }
        save.close();
    }


    public static long getShortestDistance(int[][] population, long[][] matrixOfDistances) {
        long shortestDistance = Long.MAX_VALUE;
        for (int j = 0; j < population.length; j++) {
            if (getDistatnce(population[j], matrixOfDistances) < shortestDistance) {
                shortestDistance = getDistatnce(population[j], matrixOfDistances);
            }
        }
        return shortestDistance;
    }

    public static int getShortestIndex(int[][] population, long[][] matrixOfDistances) {
        int index = 0;
        long shortestDistance = Long.MAX_VALUE;
        for (int j = 0; j < population.length; j++) {
            long currentDistance = getDistatnce(population[j], matrixOfDistances);
            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                index = j;
            }
        }
        return index;
    }

    public static int getLongestIndex(int[][] population, long[][] matrixOfDistances) {
        int index = 0;
        long longestDistance = Long.MIN_VALUE;
        for (int j = 0; j < population.length; j++) {
            long currentDistance = getDistatnce(population[j], matrixOfDistances);
            if (currentDistance > longestDistance) {
                longestDistance = currentDistance;
                index = j;
            }
        }
        return index;
    }

    public static long getDistatnce(int[] individual, long[][] matrixOfDistances) {
        int i = 0;
        long distance = 0;
        for (i = 0; i < individual.length - 1; i++) {
            distance += matrixOfDistances[individual[i]][individual[i + 1]];
        }
        distance += matrixOfDistances[individual[i]][individual[0]];
        return distance;
    }

    public static int[][] tournamentSelection(int[][] population, long[][] matrixOfDistances) {

        int[][] population2 = population.clone();


        for (int i = 0; i < population.length; i++) {

            int fighters = 3;
            int[][] fightingPopulation = new int[fighters][population[i].length];
            int[] fighersIndexes = new int[fighters];

            //to not have zeros
            for (int k = 0; k < fighersIndexes.length; k++) {
                fighersIndexes[k] = -1;
            }

            int count = 0;
            int index;
            //making sure that all indexes are different
            while (count < fighters) {
                index = random.nextInt(population.length);
                if (canAdd(index, fighersIndexes)) {
                    fighersIndexes[count] = index;
                    count++;
                }
            }

            //initialization of fighting population
            for (int j = 0; j < fightingPopulation.length; j++) {
                fightingPopulation[j] = population[fighersIndexes[j]].clone();
            }

            //checking distance of every individual
            long shortestDistance = Long.MAX_VALUE;
            int shortestIndex = 0;
            int j;
            for (j = 0; j < fightingPopulation.length; j++) {
                if (getDistatnce(fightingPopulation[j], matrixOfDistances) < shortestDistance) {
                    shortestDistance = getDistatnce(fightingPopulation[j], matrixOfDistances);
                    shortestIndex = j;
                }
            }
            population2[i] = population[fighersIndexes[shortestIndex]].clone();
        }
        //elitism, jesli najkrotszy osobnik z populacji macierzystej jest kroszty niz najdluzszy z dzieciecej, zamien go
        int shortestIndex = getShortestIndex(population, matrixOfDistances);
        int longestIndex = getLongestIndex(population2, matrixOfDistances);
        if (getDistatnce(population[shortestIndex], matrixOfDistances) < getDistatnce(population2[longestIndex], matrixOfDistances)) {
            population2[longestIndex] = population[shortestIndex].clone();
        }
        return population2;
    }


    public static boolean canAdd(int city, int[] route) {
        for (int i = 0; i < route.length; i++) {
            if (route[i] == city) return false;
        }
        return true;
    }

    public static void makeMatrixOfDistances(long[][] matrixOfDistances, long[] x, long[] y) {
        for (int i = 0; i < matrixOfDistances.length; i++) {
            for (int j = 0; j < matrixOfDistances[i].length; j++) {
                if (i == j) {
                    matrixOfDistances[i][j] = -1;
                } else
                    matrixOfDistances[i][j] = Math.abs(x[i] - x[j]) + Math.abs(y[i] - y[j]);
            }
        }

    }

    public static ArrayList<ArrayList> fileReading(String filename) {
        ArrayList<Long> x = new ArrayList<>();
        ArrayList<Long> y = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            for (int i = 0; i < 6; i++) {
                bufferedReader.readLine();
            }
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] data = input.split("\\s+");
                if (data.length < 3) break;
                x.add(Long.parseLong(data[data.length - 2]));
                y.add(Long.parseLong(data[data.length - 1]));
            }
        } catch (IOException e) {
            System.out.println("Wrong file path or name");
        }
        ArrayList<ArrayList> XiY = new ArrayList<>();
        XiY.add(0, x);
        XiY.add(1, y);
        return XiY;
    }

    public static int[][] OrderOneCrossover(int[][] population, double possibilityOfCrossing) {

        int[][] finalPopulation = population.clone();

        for (int i = 0; i < population.length; i++) {
            double x = Math.random();
            if (x <= possibilityOfCrossing) {
                int fathersIndex = random.nextInt(population.length);
                int mothersIndex = random.nextInt(population.length);
                //making sure, that indexes are different
                while (fathersIndex == mothersIndex) {
                    fathersIndex = random.nextInt(population.length);
                    mothersIndex = random.nextInt(population.length);
                }
                int start = random.nextInt(population[i].length - 1) + 1;
                int end = random.nextInt(population[i].length - 1) + 1;
                //making sure that start is before end XDDD
                while (start >= end) {
                    start = random.nextInt(population[i].length - 1) + 1;
                    end = random.nextInt(population[i].length - 1) + 1;
                }
                //creating parents
                int[] father = population[fathersIndex].clone();
                int[] mother = population[mothersIndex].clone();

                int l = father.length;

                //creating kid and "-1" trick
                int[] kid = new int[father.length];
                for (int j = 0; j < kid.length; j++) {
                    kid[j] = -1;
                }

                //copying fathers part
                for (int j = start; j <= end; j++) {
                    kid[j] = father[j];
                }

                //rest which will be added from the mother
                int[] rest = new int[l - (end - start) - 1];
                int k = 0;//adding city
                for (int j = end + 1; j < father.length; j++) {
                    if (canAdd(mother[j], kid)) {
                        rest[k] = mother[j];
                        k++;
                    }
                }
                int j = 0;
                while (k < rest.length) {
                    if (canAdd(mother[j], kid)) {
                        rest[k] = mother[j];
                        k++;
                    }
                    j++;
                }

                k = 0;//reset the counter of added items to the rest

                //filling childs end
                for (j = end + 1; j < father.length; j++) {
                    kid[j] = rest[k];
                    k++;
                }
                //filling childs beginning
                for (j = 0; j < start; j++) {
                    kid[j] = rest[k];
                    k++;
                }
                finalPopulation[i] = kid.clone();
            }
        }
        return finalPopulation;
    }

    public static void randomizePopulation(int[][] population, int b) {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[i].length; j++) {
                population[i][j] = j;
            }
        }
        for (int j = 0; j < population.length; j++) {
            shuffle(population[j]);
        }
    }

    public static void shuffle(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int randomPosition = random.nextInt(array.length);
            int temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }
    }

    public static void printIndividual(int[] individual) {
        for (int i = 0; i < individual.length; i++) {
            if (i == individual.length - 1) {
                System.out.print(individual[i]);
            } else
                System.out.print(individual[i] + "-");
        }
    }

    public static void printPopulation(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (j == a[i].length - 1) {
                    System.out.print(a[i][j]);
                } else
                    System.out.print(a[i][j] + "-");
            }
            System.out.println("");
        }
    }

    public static void inversionMutation(int[][] population, double mutationPossibilty) {
        for (int i = 0; i < population.length; i++) {
            double x = Math.random();
            if (x <= mutationPossibilty) {

                int[] array = population[i].clone();
                int l = array.length;

                int r1 = random.nextInt(l);
                int r2 = random.nextInt(l);
                //make sure that r1>r2
                while (r1 >= r2) {
                    r1 = random.nextInt(l);
                    r2 = random.nextInt(l);
                }
                //flips elements from r1-r2 inclusive
                int mid = r1 + ((r2 + 1) - r1) / 2;
                int endCount = r2;
                for (int j = r1; j < mid; j++) {
                    int tmp = array[j];
                    array[j] = array[endCount];
                    array[endCount] = tmp;
                    endCount--;
                }
                population[i] = array;
            }
        }
    }
}