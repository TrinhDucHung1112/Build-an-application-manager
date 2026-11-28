#include <iostream>
#include <fstream>
#include <cmath>
using namespace std;

int KT(int n){
    if(n<2) return 0;
    for(int i=2; i<=sqrt(n); i++){
        if(n%i==0) return 0;
    }
    return 1;
}

void swap(int *a, int *b){
    int temp=*a;
    *a=*b;
    *b=temp;
}

int main(){
    ofstream ofs;
    ofs.open("4.1.bin", ios::binary);
    if(!ofs.is_open()){
        cout<< "Khong mo duoc file";
        return 1;
    }
    int n; cin>>n;
    ofs.write((char*)&n, sizeof(int));
    int* array= new int[n];
    
    for(int i=0; i<n; i++){
        cin>>array[i];
    }
    ofs.write((char*)array, sizeof(int)*n);
    ofs.close();

    ifstream ifs;
    ifs.open("4.1.bin", ios::binary);
    if(!ifs.is_open()){
        cout<< "Khong mo duoc file";
        return 1;
    }
    int read_n;
    ifs.read((char*)&read_n, sizeof(int));
    int* read_array=new int [read_n];
    ifs.read((char*)read_array, sizeof(int)*read_n);
    ifs.close();

    fstream fs;         //có thể nhập các giá trị cần làm vô fs thay vì phải dùng binary
    fs.open("4.1.txt", ios::out);
    if(!fs.is_open()){
        cout<< "Khong mo duoc file";
        return 1;
    }

    int count=0;
    for(int i=0; i< read_n; i++){
        if(KT(read_array[i])==1)
            count++;
    }
    fs<<count;
    for(int i=0; i<read_n-1;i++){
        for(int j=i+1; j<read_n; j++){
            if(read_array[i]>read_array[j]){
                swap(&read_array[i],&read_array[j]);
            }
        }
    }
    fs<<"\n";
    for(int i=0; i<read_n; i++){
        fs<<read_array[i]<<"\t";
    }
    
    delete[] read_array;
    delete[] array; 
    return 0;
}