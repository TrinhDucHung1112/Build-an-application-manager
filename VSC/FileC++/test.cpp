#include <iostream>
#include <fstream>
#include <sstream>
using namespace std;

int main() {
    int n;
    cin >> n;

    // Cấp phát mảng 2 chiều
    int** a = new int*[n];
    for (int i = 0; i < n; i++) {
        a[i] = new int[n];
    }

    // Nhập ma trận, chỉ cho phép 0 hoặc 1
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            cin >> a[i][j];
            if (a[i][j] != 0 && a[i][j] != 1) {
                cout << "Nhap sai gia tri, nhap lai";
                j--;
            }
        }
    }

    /* =======================
       Câu a: ghi ma trận ra file
       ======================= */
    fstream fs;
    fs.open("table.txt", ios::out);
    if (!fs.is_open()) {
        cout << "Khong mo duoc";
        return 1;
    }

    fs << n << endl;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            fs << a[i][j] << "\t";
        }
        fs << "\n";
    }
    fs.close();

    /* =======================
       Mở file kết quả
       ======================= */
    fstream fs1;
    fs1.open("result.txt", ios::out);
    if (!fs1.is_open()) {
        cout << "Khong mo duoc";
        return 1;
    }

    /* =======================
       Câu b: đếm số 1 trên mỗi dòng
       ======================= */
    for (int i = 0; i < n; i++) {
        int count = 0;
        for (int j = 0; j < n; j++) {
            if (a[i][j] == 1) {
                count++;
            }
        }
        fs1 << count << "\t";
    }

    /* =======================
       Câu c: đếm số hình vuông 2x2 toàn số 1
       ======================= */
    int count = 0;
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - 1; j++) {
            if (a[i][j] == 1 &&
                a[i][j + 1] == 1 &&
                a[i + 1][j] == 1 &&
                a[i + 1][j + 1] == 1) {
                count++;
            }
        }
    }
    fs1 << "\n" << count;

    /* =======================
       Câu d: đếm số cặp phần tử bằng nhau (i < j)
       ======================= */
    int count1 = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (a[i][j] == a[j][i]) {
                count1++;
            }
        }
    }

    fs1 << endl;
    fs1 << count1;
    fs1.close();

    // Giải phóng bộ nhớ
    for (int i = 0; i < n; i++) {
        delete[] a[i];
    }
    delete[] a;

    return 0;
}
