#include <iostream>
#include <fstream>
using namespace std;

int main(){
    int m, n; cin>>m>>n;
    int** a=new int*[m];
    for(int i=0; i<m; i++){
        a[i]=new int[n];
    }
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            cin>>a[i][j];
            if(a[i][j]<1 || a[i][j]>6){
                cout<<"Nhap Sai Gia tri";
                j--;
                continue;
            }
        }
    }

    double trungbinhcong=0;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            trungbinhcong+=a[i][j];
        }
    }
    trungbinhcong=trungbinhcong/(m*n);


    fstream fs;
    fs.open("table1.txt", ios::out);
    if(!fs.is_open()){
        cout<<"Can not open";
        return 1;
    }

    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            fs<<a[i][j]<<"\t";
        }
        fs<<"\n";
    }

    fs<<"TBC: "<<trungbinhcong<<endl;
    

    fs.close();
    for(int i=0; i<m; i++){
        delete[] a[i];
    }
    delete[] a;
    return 0;
}