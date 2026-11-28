#include <iostream>
#include <fstream>
using namespace std;

int main(){
    //cau a
    fstream fs;      
    fs.open("4.2.txt", ios::out);
    if(!fs.is_open()){
        cout<< "Khong mo duoc file";
        return 1;
    }
    int n; cin>>n;
    int *Array=new int[n];
    for(int i=0; i<n; i++){
        cin>>Array[i];
    }
    int max=Array[0];
    for(int i=1; i<n; i++){
        if(max<Array[i]) max=Array[i];
    }
    fs<<max;

    fs.close();

    
    return 0;
}