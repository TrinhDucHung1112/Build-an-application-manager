//lỗi
#include <iostream>
#include <fstream>
#include <string>
using namespace std;

struct PERSON{
    string name;
    int age;

    PERSON(){}

    PERSON(string _name, int _age){
        name=_name;
        age=_age;
    }
};

int main(){
    PERSON *ps= new PERSON[2];

                                                // ps[0] = PERSON("Trinh Duc Hung", 18);    
                                                // ps[1] = PERSON("Ha Huong Giang", 18);
                                                // .....
                                                // for(int i=0; i<2; i++){
                                               //     fs << ps[i].name <<endl;
                                                //     fs << ps[i].age <<endl;
                                                // }    

    fstream fs;
    fs.open("test1.txt");
    if(!fs.is_open()){
        cout<<"Khong mo duoc file\n";
        return 1;
    }
    for(int i=0; i<2; i++){
        getline(fs, ps[i].name);
        fs>> ps[i].age;
        fs.ignore();
    } 
    fs.seekg(0);
    for(int i=0; i<2; i++){
        cout<< ps[i].name<<endl;
        cout<< ps[i].age<<endl;
    }                                     
   
    
    delete[] ps;
    fs.close();
    return 0;
}