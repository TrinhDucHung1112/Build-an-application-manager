#include <iostream>
#include <fstream>
using namespace std;

int main(){
    int m,n; cin>>m>>n;
    int** a=new int*[m];
    for(int i=0; i<m; i++){
        a[i]=new int[n];
    }
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            cin>>a[i][j];
        }
    }

    //cau a
    int max;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(max<a[i][j]) max=a[i][j];
        }
    }
    int count=0;
    for(int i=0; i<m; i++){
        for(int j=0; j<n; j++){
            if(max==a[i][j]) count++;
        }
    }

    fstream fs;
    fs.open("4.4.txt");
    if(!fs.is_open()){
        cout<<"Can not open";
        return 1;
    }
    fs<<"MAX: "<<max<<endl;
    fs<<"COUNTMAX: "<<count<<endl;

    //cau b
    for(int i=0; i<n; i++){
        int sum=0;
        for(int j=0; j<m; j++){
            sum+=a[i][j];
        }
        fs<<"SUM "<<i+1<<" : "<<sum<<"\t";
    }

    fs.close();

    for(int i=0; i<m;i++){
        delete[] a[i];
    }
    delete[] a;
    return 0;
}