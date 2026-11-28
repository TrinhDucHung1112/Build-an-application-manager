#include <iostream>
#include <fstream>
using namespace std;

int main(){
    fstream fs;
    fs.open("C:\\C++(vsc)\\FileC++\\hello\\test.txt");
    if(fs.is_open()){
        cout<<"Mo file thanh cong.\n";
    }else{
        cout<<"mo file khong thanh cong.\n";
    }

    return  0;
}