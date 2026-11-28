#include <iostream>
#include <string>
#include <sstream>
#include <cctype>
#include <fstream>
#include <map>
using namespace std;

int main(){
    string s;
    getline(cin, s);

    fstream fs;
    fs.open("input.txt");
    if(!fs.is_open()){
        cout<<"Can not open";
        return 1;
    }

    for(int i=0; i<s.length(); i++){
        int count=0;
        for(int j=0; j<s.length(); j++){
            if((int)s[i]==(int)s[j]) count++;
        }
        
        fs<<s[i]<<" "<<count<<"\t";
    }
    fs.close();

    return 0;
}