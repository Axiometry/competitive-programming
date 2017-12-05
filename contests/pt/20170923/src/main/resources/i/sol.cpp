// Author: Damian Straszak

#include <algorithm>
#include <iostream>
#define ll long long

using namespace std;


int K[100002];
char C[100002];

int main()
{
    ios_base::sync_with_stdio(0);
    int te;cin>>te;
    while(te--)
    {
    	ll sumw=0,sumb=0;
    	int n;
    	cin>>n;
    	for(int i=0;i<n;i++)
    	{
    		int k;
    		char c;
    		cin>>k>>c;
    		K[i]=k;
    		C[i]=c;
    		if (c=='W') sumw+=k;
    		else sumb+=k;
    	}
    	if (sumw==0)
    	{
    		cout<<sumb<<"\n";
    		continue;
    	}
    	if (sumb==0)
    	{
    		cout<<sumw<<"\n";
    		continue;
    	}
    	ll d=__gcd(sumw,sumb);
    	sumw/=d;
    	sumb/=d;
    	ll w=0,b=0;
    	int wyn=0;
    	for(int i=0;i<n;i++)
    	{
    		if (C[i]=='W')
    		{
    			if (b%sumb) w+=K[i];
    			else
    			{
    				if (w/sumw<b/sumb && b/sumb<=(w+K[i])/sumw) wyn++;
    				w+=K[i];
    			}
    		}
    		else
    		{
    			if (w%sumw) b+=K[i];
    			else
    			{
    				if (b/sumb<w/sumw && w/sumw<=(b+K[i])/sumb) wyn++;
    				b+=K[i];
    			}
    		}
    	}
    	cout<<wyn<<"\n";
    }

	return 0;
}


