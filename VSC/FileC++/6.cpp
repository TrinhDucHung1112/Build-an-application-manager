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
    int n=7;
    ofs.write((char*)&n, sizeof(int));

    ofs.close();

    ifstream ifs;
    ifs.open("test.bin", ios::binary);
    
    int read_n;
    ifs.read((char*)&read_n, sizeof(int));

    cout<< read_n;

    return 0;
}