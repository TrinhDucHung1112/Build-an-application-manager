#include <iostream>
#include <fstream>
#include <sstream>
#include <cctype>
#include <map>
using namespace std;

int main(){
    int n; cin>>n;
    cin.ignore();

    while(n--){
        string s;
        getline(cin,s);

        fstream fs;
        fs.open("str_input.txt", ios::app);
        if(!fs.is_open()){
            cout<<"Can not open";
            return 1;
        }

        for(int i=0; i<s.length(); i++){
            fs<<s[i];
        }
        fs<<endl;

        fs.close();

        fstream fs1;
        fs1.open("str_output.txt", ios::app);
        if(!fs1.is_open()){
            cout<<"Can not open";
            return 1;
        }

        stringstream ss(s);
        string leftword;
        while(ss >> leftword);
        fs1<<leftword<<endl;

        fs1.close();
    }

    return 0;
}