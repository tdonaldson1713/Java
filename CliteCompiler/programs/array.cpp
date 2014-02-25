int main()
{
	int num[2], temp;

	if (num[0] > num[1])
	{
		temp = num[1];
		num[1] = num[0];
		num[0] = temp;
	}
}