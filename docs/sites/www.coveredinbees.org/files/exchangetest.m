%number of days in the world
numberofdays = 200000;

%number of people
people = 100;

%starting money for each person
startmoney = 1000;

%a record of standard deviation throughout the run
deviation = zeros(1, numberofdays);

medianmez = zeros(1, numberofdays);

%a record of changing gini coefficient
%note, ginichange is taken at a thousand sample points across the run
%to keep the speed up
ginichange = zeros(1, 1000);

%Matrix of everyone's money over time
peoplemoneyovertime = zeros(people, numberofdays);


h = waitbar(0, 'money changing hands...');


%hold on;

c = zeros(1,people);
c = c + startmoney;
%c(1,1)=1000;



for days = 1: numberofdays;

    
    
    %cycle through our 20 agents
    for n = 1:people;

            r = ceil(people.*rand(1,1));
            
    %if n == r (i.e. if I'm me) don't do anything
    %also do nothing if I have no money to give
   
    
        if (n~=r && c(1,n)>0);
   
            

          %  disp('r before add = ')
          %  disp(c(1,r));
          %  disp('n before minus = ')
          %  disp(c(1,n));

          
          
            c(1,r) = c (1,r) + 1;
            c(1,n) = c (1,n) - 1;

           % disp('r after add = ')
           % disp(c(1,r));
           % disp('n after minus = ')
           % disp(c(1,n));

        %else disp('r and n were equal');
        
        end;

    %record the current money value for n person
    peoplemoneyovertime(n, days) = c(1,n);
        
        
    end;
       
    deviation(1,days) = std(c);
    
    medianmez(1,days) = median(c);
    
    waitbar(days/numberofdays);
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % calculate gini coefficient
    %
    
    % take a hundred samples over the lifetime of the model
    if (mod(days,numberofdays/1000) == 0);
    
        % First, get a ranked wealth (rw) order of people
        rw = sort(c);

        %using second equation from
        %http://en.wikipedia.org/wiki/Gini_coefficient
        %that uses ranked values
        %first, two vars for storing sums

            tops = 0;
            bottoms = 0;

            %cycle through everyone doing the top and bottom iterators
            for ginicycle = 1: people;

                tops = tops + (ginicycle * rw(1, ginicycle));
                bottoms = bottoms + rw(1,ginicycle);    

            end;

        %top is multiplied by 2, the bottom by n
        tops = tops * 2;
        bottoms = bottoms * people;

        %the final answer for G
        g = (tops/bottoms) - ((people+1)/people);

        % end gini calculate
        %%%%%%%%%%%%%%%%%%%%%

        ginichange(1,(days/(numberofdays/1000))) = g;
    
    end;
    
    
end;


subplot(231), plot(medianmez);

xlabel('days');
ylabel('money');
title('median');

subplot(232), plot(deviation);

xlabel('days');
ylabel('money');
title('standard deviation');


subplot(233), plot(ginichange);

xlabel('days');
ylabel('gini coefficient');
title('gini coefficient change over time');


subplot(234), hist(c);

xlabel('moneybands');
ylabel('number of people');
title('Distribution of money on the last day');

axisv = 1:days;

subplot(235), plot(axisv, peoplemoneyovertime);

xlabel('days');
ylabel('money');
title('individuals income');


disp('mean:');
disp(mean(c));

disp('median');
disp(median(c));

disp('standard deviation:');
disp(std(c));


close(h);




