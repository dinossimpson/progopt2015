int globalA;
int globalB;

int add10(){
	globalB = globalA + 10;
}

int setB(int a){
	globalA = globalA + a;
	add10();
}

int foo(){
	int a;
	a = 1;
	globalA = globalA - a;
	if(globalA > 100){
		foo();
	}
}

int main(){
	globalA = 1;
	globalB = 99;
	// foo();
	int localA = 5;
	setB(2);
	return globalB;
}