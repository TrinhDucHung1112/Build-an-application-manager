#include <iostream>
#include <fstream>
using namespace std;

int main(){
    ofstream ofs;
    ofs.open("test.bin", ios::binary);  //đuôi file nhị phân thường là .bin hoặc .dat nên dùng cái nào cũng được    
    if(!ofs.is_open()){
        cout<< "khong mo duoc file";
        return 1;
    }
    int n=3;
    float number[n]={1.2, 1.3, 1.4};
    ofs.write((char*)&n, sizeof(int));
    ofs.write((char*)&number, sizeof(float) * n);
    ofs.close();

    //DOC file
    ifstream ifs;
    ifs.open("test.bin", ios::binary);
    if(!ifs.is_open()){
        cout << "Khong mo duoc file";
        return 1;
    }

    int read_n;
    ifs.read((char*)&read_n, sizeof(int));

    float read_number[read_n];         //nên dùng mảng động.
    ifs.read((char*)&read_number, sizeof(float) * read_n);

    for(int i=0; i< read_n; i++){
        cout<< read_number[i]<<" ";
    }

    ifs.close();

    return 0;
}