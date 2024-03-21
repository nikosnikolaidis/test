from code_runner_files.AbstractCodeRunner import AbstractCodeRunner
import matplotlib.pyplot as plt
import numpy as np


class MyCode(AbstractCodeRunner):
    def run_code(self):

        #Read data 
        #Please, do not edit this line of code.
        data = self.dataset.__getdata__()

        #Code to run
        #Add here the code to run. You can create different functions and the results should be clear stated.
        dataset_description = describe_dataset(data=data)
        boxplots = plot_boxplots(data=data)

        #Gather results
        #The results variable should be a list of dicts. Each dict element must have the following elements:
        # "data": the results, "name": name of the results (will be used a the filename in which this resuls will be stored),
        # and "format": the format in which you want to store the results (fig, csv, txt for the moment)
        results = [
            {
                "data": dataset_description,
                "name": "dataset_description",
                "format": "csv"
            }, {
                "data": boxplots,
                "name": "boxplots",
                "format": "fig"
            }
        ]
        return results
    

def describe_dataset(data):
    return data.describe(include="all")


def plot_boxplots(data):
    numeric_cols = data.select_dtype(include=[np.number])

    fig = plt.figure()

    plt.title('Boxplots of numerical variables')
    plt.boxplot(numeric_cols.values, labels=numeric_cols.columns)
    plt.xlabel('Variables')
    plt.ylabel('Values')
    plt.xticks(rotation=45)

    return fig




