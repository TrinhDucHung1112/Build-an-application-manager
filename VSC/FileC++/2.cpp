#include <iostream>
#include <fstream>
#include <string>
using namespace std;

struct PERSON{
    string name;
    int age;
};

int main(){
    PERSON *ps = new PERSON[2];

    fstream fs("test1.txt", ios::in);
    if(!fs.is_open()){
        cout << "Khong mo duoc file\n";
        return 1;
    }

    for(int i = 0; i < 2; i++){
        getline(fs, ps[i].name);
        fs >> ps[i].age;
        fs.ignore();
    }

    for(int i = 0; i < 2; i++){
        cout << ps[i].name << endl;
        cout << ps[i].age << endl;
    }

    fs.close();
    delete[] ps;
    return 0;
}
