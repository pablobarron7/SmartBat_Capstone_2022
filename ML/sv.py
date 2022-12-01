#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import datetime
import time
from scipy.fftpack import fft
from scipy.signal import welch
from datetime import datetime
from datetime import timedelta
from numpy import diff
from decimal import Decimal, getcontext
from scipy.signal import find_peaks
from scipy.integrate import trapz
import math


get_ipython().run_line_magic('matplotlib', 'inline')
plt.style.use('ggplot')


# In[2]:


def read_data(file_path):
    column_names = ['timestamp','x-axis', 'y-axis', 'z-axis','space','xacc','yacc','zacc']
    data = pd.read_csv(file_path,header = None, names = column_names,delimiter='\t')
    return data

def feature_normalize(dataset):
    mu = np.mean(dataset,axis = 0)
    sigma = np.std(dataset,axis = 0)
    return (dataset - mu)/sigma
    
def plot_axis(ax, x, y, title):
    ax.plot(x, y)
    ax.set_title(title)
    ax.xaxis.set_visible(False)
    ax.set_ylim([min(y) - np.std(y), max(y) + np.std(y)])
    ax.set_xlim([min(x), max(x)])
    ax.grid(True)
    
def plot_dactivity(data):
    fig, (ax0, ax1, ax2, ax3, ax4, ax5) = plt.subplots(nrows = 6, figsize = (15, 10), sharex = True)
    dxgero=diff(data['x-axis'])/diff(data['timestamp'])
    dygero=diff(data['y-axis'])/diff(data['timestamp'])
    dzgero=diff(data['z-axis'])/diff(data['timestamp'])
    dxacc=diff(data['xacc'])/diff(data['timestamp'])
    dyacc=diff(data['yacc'])/diff(data['timestamp'])
    dzacc=diff(data['zacc'])/diff(data['timestamp'])
    plot_axis(ax0, data['timestamp'], dxgero, 'x-axis')
    plot_axis(ax1, data['timestamp'], dygero, 'y-axis')
    plot_axis(ax2, data['timestamp'], dzgero, 'z-axis')
    plot_axis(ax3, data['timestamp'], dxacc, 'xacc')
    plot_axis(ax4, data['timestamp'], dyacc, 'yacc')
    plot_axis(ax5, data['timestamp'], dzacc, 'zacc')
    plt.subplots_adjust(hspace=0.2)
    fig.suptitle("activity1")
    plt.subplots_adjust(top=0.90)
    plt.show()
    
def plot_activity(data):
    fig, (ax0, ax1, ax2, ax3, ax4, ax5) = plt.subplots(nrows = 6, figsize = (15, 10), sharex = True)
    plot_axis(ax0, data['timestamp'], data['x-axis'], 'x-axis')
    plot_axis(ax1, data['timestamp'], data['y-axis'], 'y-axis')
    plot_axis(ax2, data['timestamp'], data['z-axis'], 'z-axis')
    plot_axis(ax3, data['timestamp'], data['xacc'], 'xacc')
    plot_axis(ax4, data['timestamp'], data['yacc'], 'yacc')
    plot_axis(ax5, data['timestamp'], data['zacc'], 'zacc')
    plt.subplots_adjust(hspace=0.2)
    fig.suptitle("activity1")
    plt.subplots_adjust(top=0.90)
    plt.show()
    
def windows(data, size):
    start = 0
    while start < data.count():
        yield int(start), int(start + size)
        start += (size / 2)

def segment_signal(data,window_size = 90):
    segments = np.empty((0,window_size,3))
    labels = np.empty((0))
    for (start, end) in windows(data['timestamp'], window_size):
        x = data["x-axis"][start:end]
        y = data["y-axis"][start:end]
        z = data["z-axis"][start:end]
        if(len(dataset['timestamp'][start:end]) == window_size):
            segments = np.vstack([segments,np.dstack([x,y,z])])
            labels = np.append(labels,stats.mode(data["space"][start:end])[0][0])
    return segments, labels

def get_fft_values(y_values, T, N, f_s):
    f_values = np.linspace(0.0, 1.0/(2.0*T), N//2)
    fft_values_ = fft(y_values)
    fft_values = 2.0/N * np.abs(fft_values_[0:N//2])
    return f_values, fft_values

def get_psd_values(y_values, T, N, f_s):
    f_values, psd_values = welch(y_values, fs=f_s)
    return f_values, psd_values


# In[47]:


# import json
  
# # Opening JSON file
# f = open("C:\\Users\\hjk0811\\Downloads\\test.json.gz")
  
# # returns JSON object as 
# # a dictionary
# data = json.load(f)
  
# # Iterating through the json
# # list
# for i in data['emp_details']:
#     print(i)
  
# # Closing file
# f.close()

# import pandas as pd
# import gzip


# def get_contents_from_json(file_path)-> dict:
#     """
#     Reads the contents of the json file into a dict
#     :param file_path:
#     :return: A dictionary of all contents in the file.
#     """
#     try:
#         with gzip.open(file_path) as file:
#             contents = file.read()
#         return json.loads(contents.decode('UTF-8'))
#     except json.JSONDecodeError:
#         print('Error while reading json file')
#     except FileNotFoundError:
#         print(f'The JSON file was not found at the given path: \n{file_path}')


# def main(file_path: str):
#     file_contents = get_contents_from_json(file_path)
#     if not isinstance(file_contents,list):
#         # I've considered you have a JSON Array in your file
#         # if not let me know in the comments
#         raise TypeError("The file doesn't have a JSON Array!!!")
#     all_columns = file_contents[0].keys()
#     data_frame = pd.DataFrame(columns=all_columns, data=file_contents)
#     print(f'Loaded {int(data_frame.size / len(all_columns))} Rows', 'Done!', sep='\n')


# if __name__ == '__main__':
#     main(r'C:\\Users\\hjk0811\\Downloads\\23t6vdtty4ywjedjkhr5snek6e.json.gz')
# /home/ubuntu/test.json.gz

import gzip
with gzip.open('/home/ubuntu/test.json.gz', 'rb') as f:
    file_content = f.read()
    
from io import StringIO

s=str(file_content,'utf-8')

sss= s.split("\\")[1:-1]
firstNum= s.split('\\')[0]
firstNum = firstNum.split('"')[-1]
aaa = [a.replace('t', 'p') for a in sss]
aaa = [a.replace('n', 'p') for a in aaa]
aaa = [a.replace('a', 'NaN') for a in aaa]
aaa.insert(0,firstNum)



# In[48]:


aaa = [ele for ele in aaa if ele != 'p']
aaa = [a.replace('p', '') for a in aaa]
print(aaa)
df = pd.DataFrame (aaa)


# In[49]:


# import numpy as np
# n = 7 # number to be used as chunk size for the first column
# first_column_df_split = pd.concat([pd.Series(j, name='y' + str(i)) for i,j in enumerate(np.split( df[0].to_numpy(), range(n, len(df[0]), n)))], axis=1)

data = np.reshape(df.to_numpy(), (-1, 7))


# In[51]:


df = pd.DataFrame(data)


# In[53]:


column_names = ['timestamp','x-axis', 'y-axis', 'z-axis','xacc','yacc','zacc']
arr = df.to_numpy()
dataset = pd.DataFrame(arr, columns = column_names)


# In[54]:


dataset['timestamp'] = dataset['timestamp'].astype(float)
dataset['x-axis'] = dataset['x-axis'].astype(float)
dataset['y-axis'] = dataset['y-axis'].astype(float)
dataset['z-axis'] = dataset['z-axis'].astype(float)
dataset['xacc'] = dataset['xacc'].astype(float)
dataset['yacc'] = dataset['yacc'].astype(float)
dataset['zacc'] = dataset['zacc'].astype(float)


# In[55]:


dataset['timestamp']=np.arange(dataset.shape[0])+1
dataset.dropna(inplace=True)

timea = dataset.iloc[:, 0]*0.001


vx=trapz(dataset.iloc[:, 4], timea)
vy=trapz(dataset.iloc[:, 5], timea)
vz=trapz(dataset.iloc[:, 6], timea)



vall = math.sqrt(vx**2 + vy**2 + vz**2)

dataset['x-axis'] = feature_normalize(dataset['x-axis'])
dataset['y-axis'] = feature_normalize(dataset['y-axis'])
dataset['z-axis'] = feature_normalize(dataset['z-axis'])
dataset['xacc'] = feature_normalize(dataset['xacc'])
dataset['yacc'] = feature_normalize(dataset['yacc'])
dataset['zacc'] = feature_normalize(dataset['zacc'])

plot_activity(dataset)

timechange = np.asarray(dataset['timestamp'])
x=np.asarray(dataset['x-axis'])
y=np.asarray(dataset['y-axis'])
z=np.asarray(dataset['z-axis'])
xa=np.asarray(dataset['xacc'])
ya=np.asarray(dataset['yacc'])
za=np.asarray(dataset['zacc'])

fig, (ax0, ax1, ax2, ax3, ax4, ax5) = plt.subplots(nrows = 6, figsize = (15, 10), sharex = True)
aaa=diff(x)
bbb=diff(timechange)
dxgero=diff(x)/diff(timechange)
dygero=diff(y)/diff(timechange)
dzgero=diff(z)/diff(timechange)
dxacc=diff(xa)/diff(timechange)
dyacc=diff(ya)/diff(timechange)
dzacc=diff(za)/diff(timechange)
sub = timechange[:-1]
plot_axis(ax0, sub, dxgero, 'x-axis')
plot_axis(ax1, sub, dygero, 'y-axis')
plot_axis(ax2, sub, dzgero, 'z-axis')
plot_axis(ax3, sub, dxacc, 'xacc')
plot_axis(ax4, sub, dyacc, 'yacc')
plot_axis(ax5, sub, dzacc, 'zacc')
plt.subplots_adjust(hspace=0.2)
fig.suptitle("activity1")
plt.subplots_adjust(top=0.90)
plt.show()

xmax = np.argmax(dxgero)
xcut=dxgero[xmax-32:xmax+128]
timexcut=timechange[xmax-32:xmax+128]
timexcutt=timexcut[0:len(xcut)]
plt.plot(timexcutt, xcut, linestyle='-', color='blue')
plt.show()

ymax = np.argmax(dygero)
ycut=dygero[ymax-32:ymax+128]
timeycut=timechange[ymax-32:ymax+128]
timeycutt=timeycut[0:len(ycut)]
plt.plot(timeycutt, ycut, linestyle='-', color='blue')
plt.show()

zmax = np.argmax(dzgero)
zcut=dzgero[zmax-32:zmax+128]
timezcut=timechange[zmax-32:zmax+128]
timezcutt=timezcut[0:len(zcut)]
plt.plot(timezcutt, zcut, linestyle='-', color='blue')
plt.show()

xamax = np.argmax(dxacc)
xacut=dxacc[xamax-32:xamax+128]
timexacut=timechange[xamax-32:xamax+128]
timexacutt=timexacut[0:len(xacut)]
plt.plot(timexacutt, xacut, linestyle='-', color='blue')
plt.show()

yamax = np.argmax(dyacc)
yacut=dyacc[yamax-32:yamax+128]
timeyacut=timechange[yamax-32:yamax+128]
timeyacutt=timeyacut[0:len(yacut)]
plt.plot(timeyacutt, yacut, linestyle='-', color='blue')
plt.show()


zamax = np.argmax(dzacc)
zacut=dzacc[zamax-32:zamax+128]
timezacut=timechange[zamax-32:zamax+128]
timezacutt=timezacut[0:len(zacut)]
plt.plot(timezacutt, zacut, linestyle='-', color='blue')
plt.show()

xmax = np.argmax(dxgero)
xcut=x[xmax-32:xmax+128]
timexcut=timechange[xmax-32:xmax+128]
timexcutt=timexcut[0:len(xcut)]
plt.plot(timexcutt, xcut, linestyle='-', color='blue')
plt.show()

ymax = np.argmax(dygero)
ycut=y[ymax-32:ymax+128]
timeycut=timechange[ymax-32:ymax+128]
timeycutt=timeycut[0:len(ycut)]
plt.plot(timeycutt, ycut, linestyle='-', color='blue')
plt.show()

zmax = np.argmax(dzgero)
zcut=z[zmax-32:zmax+128]
timezcut=timechange[zmax-32:zmax+128]
timezcutt=timezcut[0:len(zcut)]
plt.plot(timezcutt, zcut, linestyle='-', color='blue')
plt.show()

xamax = np.argmax(dxacc)
xacut=xa[xamax-32:xamax+128]
timexacut=timechange[xamax-32:xamax+128]
timexacutt=timexacut[0:len(xacut)]
plt.plot(timexacutt, xacut, linestyle='-', color='blue')
plt.show()

yamax = np.argmax(dyacc)
yacut=ya[yamax-32:yamax+128]
timeyacut=timechange[yamax-32:yamax+128]
timeyacutt=timeyacut[0:len(yacut)]
plt.plot(timeyacutt, yacut, linestyle='-', color='blue')
plt.show()


zamax = np.argmax(dzacc)
zacut=za[zamax-32:zamax+128]
timezacut=timechange[zamax-32:zamax+128]
timezacutt=timezacut[0:len(zacut)]
plt.plot(timezacutt, zacut, linestyle='-', color='blue')
plt.show()

r = 5.25
m = 1115
vcutx = trapz(xacut, timexacutt)
vcuty= trapz(yacut, timeyacut)
vcutz=trapz(zacut, timezacut)

impx = r*m* vcutx
impy = r*m* vcuty
impz = r*m* vcutz

angx=trapz(xcut, timexcut)
angy=trapz(ycut, timeycut)
angz=trapz(zcut, timezcut)

t_n = 10
N = 1000
T = t_n / N
f_s = 1/T

f_values, fft_xvalues = get_fft_values(xcut, T, N, f_s)
fx_values=f_values[0:len(fft_xvalues)]
plt.plot(fx_values, fft_xvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()

f_values, fft_yvalues = get_fft_values(ycut, T, N, f_s)
fx_values=f_values[0:len(fft_yvalues)]
plt.plot(fx_values, fft_yvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()

f_values, fft_zvalues = get_fft_values(zcut, T, N, f_s)
fx_values=f_values[0:len(fft_zvalues)]
plt.plot(fx_values, fft_zvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()

f_values, fft_xavalues = get_fft_values(xacut, T, N, f_s)
fx_values=f_values[0:len(fft_xavalues)]
plt.plot(fx_values, fft_xavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()

f_values, fft_yavalues = get_fft_values(yacut, T, N, f_s)
fx_values=f_values[0:len(fft_yavalues)]
plt.plot(fx_values, fft_yavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()

f_values, fft_zavalues = get_fft_values(zacut, T, N, f_s)
fx_values=f_values[0:len(fft_zavalues)]
plt.plot(fx_values, fft_zavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]', fontsize=16)
plt.ylabel('Amplitude', fontsize=16)
plt.title("Frequency domain of the signal", fontsize=16)
plt.show()


fftx2 = pow(fft_xvalues,2)
ffty2 = pow(fft_yvalues,2)
fftz2 = pow(fft_zvalues,2)
fftxa2 = pow(fft_xavalues,2)
fftya2 = pow(fft_yavalues,2)
fftza2 = pow(fft_zavalues,2)

sumx = 0
for i in fftx2:
        sumx = sumx + i
sumy = 0
for i in ffty2:
        sumy = sumy + i
sumz = 0
for i in fftz2:
        sumz = sumz + i
sumxa = 0
for i in fftxa2:
        sumxa = sumxa + i
sumya = 0
for i in fftya2:
        sumya = sumya + i
sumza = 0
for i in fftza2:
        sumza = sumza + i

t_n = 10
N = 1000
T = t_n / N
f_s = 1/T

f_values, psd_xvalues = get_psd_values(xcut, T, N, f_s)
fx_values=f_values[0:len(psd_xvalues)]
plt.plot(fx_values, psd_xvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

f_values, psd_yvalues = get_psd_values(ycut, T, N, f_s)
fx_values=f_values[0:len(psd_yvalues)]
plt.plot(fx_values, psd_yvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

f_values, psd_zvalues = get_psd_values(zcut, T, N, f_s)
fx_values=f_values[0:len(psd_zvalues)]
plt.plot(fx_values, psd_zvalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

f_values, psd_xavalues = get_psd_values(xacut, T, N, f_s)
fx_values=f_values[0:len(psd_xavalues)]
plt.plot(fx_values, psd_xavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

f_values, psd_yavalues = get_psd_values(yacut, T, N, f_s)
fx_values=f_values[0:len(psd_yavalues)]
plt.plot(fx_values, psd_yavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

f_values, psd_zavalues = get_psd_values(zacut, T, N, f_s)
fx_values=f_values[0:len(psd_zavalues)]
plt.plot(fx_values, psd_zavalues, linestyle='-', color='blue')
plt.xlabel('Frequency [Hz]')
plt.ylabel('PSD [V**2 / Hz]')
plt.show()

#fft peaks
peaks, _ = find_peaks(fft_xvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0, 0, 0, 0, 0])
plt.plot(fft_xvalues)
plt.plot(firstFivepeaks, fft_xvalues[firstFivepeaks], "x")
plt.plot(np.zeros_like(fft_xvalues), "--", color="gray")
plt.show()
xfftFive=firstFivepeaks.tolist()
xfftFiveY=fft_xvalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(fft_yvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])
plt.plot(fft_yvalues)
plt.plot(firstFivepeaks, fft_yvalues[firstFivepeaks], "x")
plt.plot(np.zeros_like(fft_yvalues), "--", color="gray")
plt.show()
yfftFive=firstFivepeaks.tolist()
yfftFiveY=fft_yvalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(fft_zvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])
plt.plot(fft_zvalues)
plt.plot(firstFivepeaks, fft_zvalues[firstFivepeaks], "x")
plt.plot(np.zeros_like(fft_zvalues), "--", color="gray")
plt.show()
zfftFive=firstFivepeaks.tolist()
zfftFiveY=fft_zvalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(fft_xavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])
plt.plot(fft_xavalues)
plt.plot(firstFivepeaks, fft_xavalues[firstFivepeaks], "x")
plt.plot(np.zeros_like(fft_xavalues), "--", color="gray")
plt.show()
xafftFive=firstFivepeaks.tolist()
xafftFiveY=fft_xavalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(fft_yavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])
plt.plot(fft_yavalues)
plt.plot(firstFivepeaks, fft_yavalues[firstFivepeaks], "x")
plt.plot(np.zeros_like(fft_yavalues), "--", color="gray")
plt.show()
yafftFive=firstFivepeaks.tolist()
yafftFiveY=fft_yavalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(fft_zavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

zafftFive=firstFivepeaks.tolist()
zafftFiveY=fft_zavalues[firstFivepeaks].tolist()

#psd peaks
peaks, _ = find_peaks(psd_xvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

xpsdFive=firstFivepeaks.tolist()
xpsdFiveY=psd_xvalues[firstFivepeaks].tolist()
print(xpsdFive)
print(xpsdFiveY)

peaks, _ = find_peaks(psd_yvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

ypsdFive=firstFivepeaks.tolist()
ypsdFiveY=psd_yvalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(psd_zvalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

zpsdFive=firstFivepeaks.tolist()
zpsdFiveY=psd_zvalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(psd_xavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

xapsdFive=firstFivepeaks.tolist()
xapsdFiveY=psd_xavalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(psd_yavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

yapsdFive=firstFivepeaks.tolist()
yapsdFiveY=psd_yavalues[firstFivepeaks].tolist()

peaks, _ = find_peaks(psd_zavalues, height=0)
firstFivepeaks = peaks[0:5]
while len(firstFivepeaks) < 5:
    if(len(firstFivepeaks)!=0):
        firstFivepeaks = np.append(firstFivepeaks, firstFivepeaks[len(firstFivepeaks)-1])
    else:
        firstFivepeaks = np.array([0,0,0,0,0])

zapsdFive=firstFivepeaks.tolist()
zapsdFiveY=psd_zavalues[firstFivepeaks].tolist()

list_of_features = []
for i in range (5):
    list_of_features.append(xfftFive[i])
    list_of_features.append(xfftFiveY[i])
    list_of_features.append(yfftFive[i])
    list_of_features.append(yfftFiveY[i])
    list_of_features.append(zfftFive[i])
    list_of_features.append(zfftFiveY[i])
    list_of_features.append(xafftFive[i])
    list_of_features.append(xafftFiveY[i])
    list_of_features.append(yafftFive[i])
    list_of_features.append(yafftFiveY[i])
    list_of_features.append(zafftFive[i])
    list_of_features.append(zafftFiveY[i])
    list_of_features.append(xpsdFive[i])
    list_of_features.append(xpsdFiveY[i])
    list_of_features.append(ypsdFive[i])
    list_of_features.append(ypsdFiveY[i])
    list_of_features.append(zpsdFive[i])
    list_of_features.append(zpsdFiveY[i])
    list_of_features.append(xapsdFive[i])
    list_of_features.append(xapsdFiveY[i])
    list_of_features.append(yapsdFive[i])
    list_of_features.append(yapsdFiveY[i])
    list_of_features.append(zapsdFive[i])
    list_of_features.append(zapsdFiveY[i])



list_of_features.append(sumx)
list_of_features.append(sumy)
list_of_features.append(sumz)
list_of_features.append(sumxa)
list_of_features.append(sumya)
list_of_features.append(sumza)

list_of_features.append(impx)
list_of_features.append(impy)
list_of_features.append(impz)


# In[56]:


array2d=[[0]]
array2d[0]=list_of_features



columnNames=['1','2','3']
for i in range(4,130):
  columnNames.append(str(i))

df = pd.DataFrame(array2d, columns = columnNames)


from joblib import Parallel, delayed
import joblib

# Load the model from the file
clf_from_joblib = joblib.load('model1.pkl')
  
# Use the loaded model to make predictions
region = int(clf_from_joblib.predict(df))


# In[57]:


import json
 
# Data to be written
dictionary = {
    "region": region,
    "speedoverall": vall,
    "speedimpactx": vcutx,
    "speedimpacty": vcuty,
    "speedimpactz": vcutz,
    "anglex":angx,
    "angley":angy,
    "anglez":angz
}

class NpEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        if isinstance(obj, np.floating):
            return float(obj)
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)


json_str = json.dumps(dictionary, cls=NpEncoder)

with open("/home/ubuntu/sample.json", "w") as outfile:
    outfile.write(json_str)


# In[ ]:


print(dictionary)


# In[ ]:




