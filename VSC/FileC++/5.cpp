// • ios::in: Mở file ở chế độ đọc nếu file đã tồn tại.    = ifstream      
// • ios::out: Mở file ở chế độ ghi và xóa hết nội dung trong file, nếu file không tồn tại thì sẽ tạo file.  = ofstream
// • ios::trunc: Mở file ở chế độ ghi và xóa hết nội dung trong file, nếu file không tồn tại thì sẽ tạo file.
// • ios::app: Mở file ở chế độ ghi và sẽ ghi vào cuối chứ không xóa các dữ liệu cũ.
// • ios::ate: Mở file và di chuyển con trỏ xuống cuối file.
// • ios::binary: Làm việc với file nhị phân.

#include <iostream>
#include <fstream>
#include <string>
using namespace std;

int main(){
    fstream fs;
    fs.open("test.txt", ios::in | ios::ate);                                 
                                                

    if(!fs.is_open()){
        cout << "Khong mo duoc file\n";
        return 1;
    }

    string str;
    getline(fs,str);
    cout<<str;

    fs.close();
    return 0;
}