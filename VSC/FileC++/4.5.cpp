#include <iostream>
#include <fstream>
using namespace std;

int main(){
    int n; cin>>n;
    int** a=new int*[n];
    for(int i=0; i<n; i++){
        a[i]=new int[n];
    }
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            cin>>a[i][j];
            if(a[i][j]!=0 && a[i][j]!=1){
                cout<<"Nhap Sai Gia tri";
                j--;
                continue;
            }
        }
    }

    int countone=0;
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            if(a[i][j]==1) countone++;
        }
    }

    fstream fs;
    fs.open("table.txt", ios::out);
    if(!fs.is_open()){
        cout<<"Can not open";
        return 1;
    }

    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            fs<<a[i][j]<<"\t";
        }
        fs<<"\n";
    }

    fs<<countone<<endl;
    

    fs.close();
    for(int i=0; i<n; i++){
        delete[] a[i];
    }
    delete[] a;
    return 0;
}