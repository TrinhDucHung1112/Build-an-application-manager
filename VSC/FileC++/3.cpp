#include <iostream>
#include <fstream>
#include <string>
using namespace std;

int main(){
    fstream fs;
    fs.open("test1.txt");
    if(!fs.is_open()){
        return 1; // nhằm ám chỉ là đã thực hiện xong nhưng có lỗi khoogn mở được file2
    }
    //ghi file

    fs << "Ha Huong Giang"<<endl;
    fs << 1; 
    // QUAN TRỌNG: quay về đầu file
    fs.seekg(0);
    //Doc FILE
    string name;
    int a;
    getline(fs, name);
    fs>>a;
    cout<<"Name:  "<<name<<endl;
    cout<<"Number: "<<a;
    
    
    fs.close();
    return  0;
}