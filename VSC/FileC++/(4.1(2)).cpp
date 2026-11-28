#include <iostream>
#include <fstream>
#include <cmath>
using namespace std;

int isPrime(int n) {
    if (n < 2) return 0;
    for (int i = 2; i <= sqrt(n); i++)
        if (n % i == 0) return 0;
    return 1;
}

int main() {
    ifstream fin("4.1.txt");
    ofstream fout("4.1_out.txt");

    if (!fin.is_open()) {
        cout << "Khong mo duoc file 4.1.txt";
        return 0;
    }

    int n;
    fin >> n;

    int* a = new int[n];
    for (int i = 0; i < n; i++)
        fin >> a[i];

    int cnt = 0;
    for (int i = 0; i < n; i++)
        if (isPrime(a[i])) cnt++;

    for (int i = 0; i < n - 1; i++)
        for (int j = i + 1; j < n; j++)
            if (a[i] > a[j])
                swap(a[i], a[j]);

    fout << cnt << endl;
    for (int i = 0; i < n; i++)
        fout << a[i] << " ";

    delete[] a;
    fin.close();
    fout.close();
    return 0;
}
