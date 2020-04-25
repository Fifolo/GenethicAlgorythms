package com.starczi;

import jdk.jshell.execution.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static Random random = new Random();
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) throws FileNotFoundException {
        int b, c, liczbaGeneracji, liczbaOsobników;

        float a = (float) 4;
//        while (true) {
//            a = scanner.nextInt();
//            scanner.nextLine();
//            if (a > 0) {
//                break;
//            }
//            System.out.println("Podaj wartosc wieksza od 0");
//        }
        b = 7;
        c = 2;
        while (true) {
            System.out.println("Podaj liczbe generacji: ");
            liczbaGeneracji = scanner.nextInt();
            System.out.println("Podaj liczbe osobników: ");
            liczbaOsobników = scanner.nextInt();
            if (liczbaGeneracji * liczbaOsobników <= 150) {
                break;
            }
            System.out.println("Podaj liczbe generacji i osobników tak" + "\n" + ",aby ich iloczyn <= 150");
        }

        int liczbaUruchomienProgramu = 40;

        double pKrzyzowania = 0.8;

        double pMutacji = 0.1;

        //tworzenie populacji

//        ArrayList<StringBuilder> populacja = new ArrayList<StringBuilder>(liczbaOsobników);
//        for (int i = 0; i < liczbaOsobników; i++) {
//            populacja.add(new StringBuilder(8));
//            for (int j = 0; j < populacja.get(i).capacity(); j++) {
//                populacja.get(i).insert(j, random.nextInt(2));
//            }
//        }


        PrintWriter zapis = new PrintWriter("wyniki.txt");

        for (int i = 1; i <= liczbaUruchomienProgramu; i++) {

            //tworzenie populacji

            ArrayList<StringBuilder> populacja = new ArrayList<StringBuilder>(liczbaOsobników);

            for (int k = 0; k < liczbaOsobników; k++) {
                populacja.add(new StringBuilder(8));
                for (int j = 0; j < populacja.get(k).capacity(); j++) {
                    populacja.get(k).insert(j, random.nextInt(2));
                }
            }


            //dokonanie selekcji,mutacji i krzyzowania
            for (int j = 0; j < liczbaGeneracji; j++) {

                populacja = Selekcja(populacja, a, b, c);
                System.out.println("Populacja po selekcji: ");
                wypiszPopulacje(populacja);
                System.out.println("");

                populacja = Mutacja(populacja, pMutacji);
                System.out.println("Populacja po mutacji: ");
                wypiszPopulacje(populacja);
                System.out.println("");

                populacja = Krzyzowanie(populacja, pKrzyzowania);
                System.out.println("Populacja po krzyzowaniu: ");
                wypiszPopulacje(populacja);

                System.out.println("");
                System.out.println("Nastepna generacja");
                System.out.println("");
            }
            System.out.println("Ostateczna populacja to: ");
            wypiszPopulacje(populacja);

//            int best = 0;
//            for (int p = 0; p < populacja.size(); p++) {
//                if (wyliczFunkcjeOsobnika(binaryToInt(populacja.get(p)),a,b,c) > best) {
//                    best = binaryToInt(populacja.get(p));
//                }
//            }

            //    zapis.append("Wynik "+i+"-ego dzialania programu \n\n ");

            //wypisanie funkcji przystosowania
            zapis.print(getFittest(populacja, a, b, c) + " ");
            //wypisanie osobnika
            zapis.print(binaryToInt(populacja.get(getFittestIndex(populacja, a, b, c))));

            zapis.println("");
            //zapis.append("\n");
            if (i == liczbaUruchomienProgramu) {
                zapis.close();
            }
            //System.out.println("wartosc funkcji najlepszego osobnika = " + getFittest(populacja, a, b, c));
            //System.out.println("najlepszy osobnik to ==> " + populacja.get(getFittestIndex(populacja, a, b, c)));
            // int liczba = binaryToInt(populacja.get(3));
        }
    }

    public static int getFittestIndex(ArrayList<StringBuilder> populacja, float a, int b, int c) {
        float liczba = Integer.MIN_VALUE;
        int j = 0;
        for (int i = 0; i < populacja.size(); i++) {
            if (wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c) > liczba) {
                liczba = (wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c));
                j = i;
            }
        }
        return j;
    }

    public static float getFittest(ArrayList<StringBuilder> populacja, float a, int b, int c) {
        float liczba = Integer.MIN_VALUE;
        for (int i = 0; i < populacja.size(); i++) {
            if (wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c) > liczba) {
                liczba = (wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c));
            }
        }
        return liczba;
    }

    public static void wypiszPopulacje(ArrayList<StringBuilder> populacja) {
        for (int i = 0; i < populacja.size(); i++) {
            System.out.println("osobnik " + i + " ==> " + populacja.get(i));
        }
    }

    public static int binaryToInt(StringBuilder osobnik) {
        int liczba = Integer.parseInt(osobnik.toString(), 2);
        return liczba;
    }

    public static float wyliczFunkcjeOsobnika(float x, float a, int b, int c) {
        return a * (x * x) + (x * b) + c;
    }

    public static float wyliczFunkcjePopulacji(ArrayList<StringBuilder> populacja, float a, int b, int c) {
        float suma = 0;
        float x = 0;
        for (int i = 0; i < populacja.size(); i++) {
            x = wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c);
            suma += x;
        }
        return suma;
    }

    public static ArrayList<StringBuilder> Mutacja(ArrayList<StringBuilder> populacja, double pMutacji) {
        for (int i = 0; i < populacja.size(); i++) {
            for (int j = 0; j < 8; j++) {
                double x = Math.random();
                if (x <= pMutacji) {
                    if (populacja.get(i).charAt(j) == '0') {
                        populacja.get(i).setCharAt(j, '1');
                    } else populacja.get(i).setCharAt(j, '0');
                }
            }
        }
        return populacja;
    }

    public static ArrayList<StringBuilder> Selekcja(ArrayList<StringBuilder> populacja, float a, int b, int c) {
        float funkcjaCelu = 0;
        float najgorszyOsobnik = Integer.MAX_VALUE;
        //wartosc osobnika f(x)
        float x;
        ArrayList<StringBuilder> finalnaLista = new ArrayList<>();

        //wyliczenie najgorszego osobnika
        for (int i = 0; i < populacja.size(); i++) {
            //   System.out.println("osobnik " + i + " ==>" + populacja.get(i) + " ma wartosc " + binaryToInt(populacja.get(i)));
            x = wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c);
            //    System.out.println("wartosc funkcji osobnika " + i + "==>" + x);
            if (x < najgorszyOsobnik) {
                najgorszyOsobnik = x;
            }
        }
        //  System.out.println("Najgorszy osobnik = " + najgorszyOsobnik);
        //  System.out.println("");
        if (najgorszyOsobnik < 0) {
            // pracuj z pomocniczą (wystapily ujemne wartosci)
            double[] wartosciFunkcjiOsobnikow = new double[populacja.size()];
            //     System.out.println("");
            for (int j = 0; j < populacja.size(); j++) {
                x = wyliczFunkcjeOsobnika(binaryToInt(populacja.get(j)), a, b, c) - najgorszyOsobnik + 1;
                wartosciFunkcjiOsobnikow[j] = x;
                funkcjaCelu += x;
            }
            //System.out.println("funkcja celu = " + funkcjaCelu);
            //System.out.println("");
            // UZUPELNIANIE POPULACJI
            double wkladOsobnika = 0;
            for (int j = 0; j < populacja.size(); j++) {
                {

                    double losowaLiczba = Math.random();
                    //        System.out.println("losowa liczba = " + losowaLiczba + "  " + j);

                    for (int i = 0; i < populacja.size(); i++) {

                        wkladOsobnika += wartosciFunkcjiOsobnikow[i] / funkcjaCelu;
                        //              System.out.println("wklad osobnika " + i + " ==> " + wkladOsobnika);


                        if (losowaLiczba <= wkladOsobnika) {
                            //wez osobnika N , zamień go na wylosowanego w kole ruletki
                            //                    System.out.println("wybrano " + i + "-ego osobnika");
                            finalnaLista.add(j, populacja.get(i));
                            wkladOsobnika = 0;
                            break;
                        }
                    }
                }
            }
            return finalnaLista;
        }

        // jesli nie ma osobnikow ujemnych
        else {
            //      System.out.println(" ");

            funkcjaCelu = wyliczFunkcjePopulacji(populacja, a, b, c);

            double wkladOsobnika = 0;
            //        System.out.println("funkcja celu = " + funkcjaCelu);
            //          System.out.println("");

            for (int j = 0; j < populacja.size(); j++) {

                double losowaLiczba = Math.random();
//                System.out.println("losowa liczba = " + losowaLiczba + " " + j);

                for (int i = 0; i < populacja.size(); i++) {
                    wkladOsobnika += wyliczFunkcjeOsobnika(binaryToInt(populacja.get(i)), a, b, c) / funkcjaCelu;
                    //System.out.println("wklad osobnika " + i + " ==> " + wkladOsobnika);

                    if (losowaLiczba <= wkladOsobnika) {
                        finalnaLista.add(j, populacja.get(i));
                        // populacja.remove(j);
                        // populacja.add(j, temp);
                        //System.out.println("wybrano " + i + "-ego osobnika");
                        //System.out.println("");
                        wkladOsobnika = 0;
                        break;
                    }
                }
            }
            System.out.println(" ");
            return finalnaLista;
        }
    }

    public static ArrayList<StringBuilder> Krzyzowanie(ArrayList<StringBuilder> populacja, double pKrzyzowania) {

        ArrayList<StringBuilder> nowaPopulacja = new ArrayList<>(populacja.size());

        while (populacja.size() > 0) {

            if (Math.random() <= pKrzyzowania) {

                if (populacja.size() / 2 != 0) {
                    //czy jest mozliwosc parowania?

                    // pkt przeciecia oraz dzieci ktore przejma geny po rodzicach

                    int punktPrzeciecia = random.nextInt(7) + 1;
                    //   System.out.println("");
                    StringBuilder dziecko = new StringBuilder(8);
                    StringBuilder drugieDziecko = new StringBuilder(8);
                    //   System.out.println("punkt przeciecia = " + punktPrzeciecia);

                    int indeksOjca = random.nextInt(populacja.size());
                    //   System.out.println("indeks ojca = " + indeksOjca);
                    StringBuilder czescOjca = new StringBuilder();
                    czescOjca.append(populacja.get(indeksOjca));
                    populacja.remove(indeksOjca);

                    int indeksMatki = random.nextInt(populacja.size());
                    //   System.out.println("indeks matki = " + indeksMatki);
                    StringBuilder czescMatki = new StringBuilder();
                    czescMatki.append(populacja.get(indeksMatki));
                    populacja.remove(indeksMatki);


                    for (int i = 0; i < punktPrzeciecia; i++) {
                        dziecko.append(czescOjca.charAt(i));
                        drugieDziecko.append(czescMatki.charAt(i));
                    }
                    for (int i = punktPrzeciecia; i < dziecko.capacity(); i++) {
                        dziecko.append(czescMatki.charAt(i));
                        drugieDziecko.append(czescOjca.charAt(i));
                    }

                    nowaPopulacja.add(dziecko);
                    nowaPopulacja.add(drugieDziecko);

                }
                // jezeli zostal ostatni osobnik, dodajemy go bez krzyzowania
                else {
                    nowaPopulacja.add(populacja.get(0));
                    populacja.remove(0);
                }

            }
            // jezeli nie krzyzujemy to dodajemy rodzicow jako dzieci
            else {
                if (populacja.isEmpty())
                // sprawdzamy czy jest kogo dodac
                {
                    // jak zostalo wiecej niz 1
                    if (populacja.size() > 1) {
                        int losowyOsobnik = random.nextInt(populacja.size());
                        nowaPopulacja.add(populacja.get(losowyOsobnik));
                        populacja.remove(losowyOsobnik);

                        losowyOsobnik = random.nextInt(populacja.size());
                        nowaPopulacja.add(populacja.get(losowyOsobnik));
                        populacja.remove(losowyOsobnik);
                    } else {
                        nowaPopulacja.add(populacja.get(0));
                        populacja.remove(0);
                    }
                }
            }
        }
        return nowaPopulacja;

    }
}


