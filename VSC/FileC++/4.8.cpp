#include <iostream>
#include <fstream>
#include <sstream>
#include <cctype>
#include <climits>
#include <cmath>
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
    int** a= new int*[m];
    for(int i=0; i<m; i++){
        a[i]=new int[n];
    }

    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            cin>>a[i][j];
        }
    }

    int MAX=INT_MIN;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(MAX<a[i][j]) MAX=a[i][j];
        }
    }

    fstream fs;
    fs.open("table3.txt");
    if(!fs.is_open()){
        cout<<"Can not open";
        return 1;
    }

    //cau a
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            fs<<a[i][j]<<"\t";
        }
        fs<<"\n";
    }
    fs<<MAX<<" ";

    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(MAX==a[i][j]) fs<<i<<" "<<j<<" ";
        }
    }  
    fs<<endl;

    //cau b
    for(int i=0; i<m; i++){
        int sum=0;
        for(int j=0; j<n; j++){
            sum+=a[i][j];
        }
        fs<<sum<<" ";
    }
    fs<<endl;
    
    //cau c;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(KT(a[i][j])==1){
                  
            }
        }
    }


    fs.close();


    for(int i=0; i<m; i++){
        delete[] a[i]; 
    }
    delete[] a;
    return 0;
}