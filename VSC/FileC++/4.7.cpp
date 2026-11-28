#include <iostream>
#include <fstream>
using namespace std;

int main(){
    int n; cin>>n;
    int**a=new int*[n];
    for(int i=0; i<n; i++){
        a[i]=new int[n];
    }
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            cin>>a[i][j];
            if(a[i][j]!=0 && a[i][j]!=1){
                cout<<"nhap sai gia tri";
                j--;
                continue;
            }
        }
    }
    int temp;
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            if(a[i][j]==a[j][i] &&  i!=j){
                temp=1;
                break;
            }else if(a[i][j]!=a[j][i] &&  i!=j){
                temp=0;
                break;
            }
        }
    }
    int max;
    
    



    

    fstream fs;
    fs.open("table2.txt");
    if(!fs.is_open()){
        cout<<"Khong mo duoc file";
        return 1;
    }
    
    for(int i=0; i<n; i++){
        for(int j=0; j<n; j++){
            fs<<a[i][j]<<"\t";
        }
        fs<<"\n";
    }
    
    for(int i=0; i<n; i++){
        int sum=0;
        for(int j=0; j<n; j++){
            sum+=a[i][j];
        }  
        fs<<sum<<"\t";
    }
    fs<<"\n"<<temp<<endl;

    fs.close();

    for(int i=0; i<n; i++){
        delete[] a[i];
    } 
    delete[] a;
    return 0;
}