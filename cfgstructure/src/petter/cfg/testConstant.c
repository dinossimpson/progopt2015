int globa;

// int bar(){
// 	int b = -1;
// 	b = 2;
// 	globa = 1;
// 	int c;
// 	c = bar2();
// 	int a = 1 + b;
// 	// b = a;
// 	if(b == 1){
// 		globa = 4;
// 		return b;
// 	}
// 	else{
// 		return a;
// 	}
// 	b = foo(2);
// }


int set123(){
	int abc = 123;
	return abc;
}

int bar2(){
	int b = -121;
	int c = 24;
	int a = 5;
	globa = -2;
	bar2();
}

// int foo(int a){
// 	a = 2;
// 	// bar();
// 	a = foo(3);
// 	if(a > 2)
// 		return 1;
// 	else
// 		return 2;
// }

int main(){
	int a, b;
	a = set123();
	if(a == 1){
		b = 1;
	}
	else{
		b = 2;
	}
	bar2();
	return a;
}
