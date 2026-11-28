#include <iostream>
#include <fstream>
#include <cmath>
#include <limits>
using namespace std;

int KT(int n){
    if(n<2) return 0;
    for(int i=2; i<=sqrt(n); i++){
        if(n%i==0) return 0;
    }
    return 1;
}

int main(){
    int m,n; cin>>m>>n;
    int** Array=new int*[m];
    for(int i=0; i<m; i++){
        Array[i]=new int[n];
    }
    for(int i=0; i<m; i++){
        for(int j=0; j<n;j++){
            cin>>Array[i][j];
        }
    }

    int count=0;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(KT(Array[i][j])==1){
                count++;
            }
        }
    }

    int maxsum;
    for(int i=0; i<m; i++){
        int sum=0;
        for(int j=0; j<n; j++){
            sum+=Array[i][j];
        }
        if(maxsum<sum){
            maxsum=sum;
        }
    }
    
    int minsum=INT_MAX;
    for(int j=0; j<n; j++){
        int sum=0;
        for(int i=0; i<m; i++){
            sum+=Array[i][j];
        }
        if(minsum>sum){
            minsum=sum;
        }
    }


    fstream fs;
    fs.open("4.3.txt", ios::out);
    if(!fs.is_open()){
        cout<<"File khong mo duoc";
        return 1;
    }
    for(int i=0; i<m; i++){
        for(int j=0; j<n;j++){
            fs<<Array[i][j]<<"\t";
        }
        fs<<"\n";
    }

    fs<<count<<endl;
    fs<<maxsum<<endl;
    fs<<minsum;

    for(int i=0; i<m; i++){
        delete[] Array[i];
    }
    delete[] Array;
    return 0;
}