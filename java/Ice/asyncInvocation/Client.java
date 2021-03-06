// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import Demo.*;

public class Client extends com.zeroc.Ice.Application
{
    @Override
    public int run(String[] args)
    {
        CalculatorPrx calculator = CalculatorPrx.checkedCast(communicator().propertyToProxy("Calculator.Proxy"));

        // Asynchronously calculate a difference
        try
        {
            CompletableFuture<Integer> result = calculator.subtractAsync(10, 4);

            // Block until the call is completed
            System.out.println("10 minus 4 is " + result.get());
        }
        catch(ExecutionException | InterruptedException exception)
        {
            exception.printStackTrace();
        }

        // Async division
        CompletableFuture<Calculator.DivideResult> result = calculator.divideAsync(13, 5);

        // Pass lambda to process result when available
        result.whenComplete((completed, exception) ->
                            {
                                if(exception != null)
                                {
                                    if(exception instanceof DivideByZeroException)
                                    {
                                        System.out.println("You cannot divide by 0");
                                    }
                                    else
                                    {
                                        exception.printStackTrace();
                                    }
                                }
                                else
                                {
                                    System.out.println("13 / 5 is " + completed.returnValue + " with a remainder of " + completed.remainder);
                                }
                            });

        // Same with divide by 0:
        CompletableFuture<Calculator.DivideResult> result2 = calculator.divideAsync(13, 0);

        result2.whenComplete((completed, exception) ->
                             {
                                 if(exception != null)
                                 {
                                     if(exception instanceof DivideByZeroException)
                                     {
                                         System.out.println("You cannot divide by 0");
                                     }
                                     else
                                     {
                                         exception.printStackTrace();
                                     }
                                 }
                                 else
                                 {
                                     System.out.println("13 / 0 is " + completed.returnValue + " with a remainder of " + completed.remainder);
                                 }
                             });

        // Combine async calls
        try
        {
            CompletableFuture<Integer> side1 = calculator.squareAsync(6);
            CompletableFuture<Integer> side2 = calculator.squareAsync(8);
            CompletableFuture<Integer> sum = side1.thenCombineAsync(side2, calculator::addAsync).get();

            CompletableFuture<Double> hypotenuse = calculator.squareRootAsync(sum.get());
            System.out.println("The hypotenuse of a triangle with side lengths of 6 and 8 is " + hypotenuse.get());
        }
        catch(ExecutionException exception)
        {
            if(exception.getCause() instanceof NegativeRootException)
            {
                System.out.println("You cannot take the square root of negative numbers");
            }
            else
            {
                exception.printStackTrace();
            }
        }
        catch(InterruptedException exception)
        {
            // Ignored
        }

        calculator.shutdown();
        return 0;
    }

    public static void main(String[] args)
    {
        Client app = new Client();
        int status = app.main("Computation Client", args, "config.client");

        System.exit(status);
    }
}
