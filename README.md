# Mark Visualization

## Introdution
The Mark Management Application is a tool designed to keep track of the courses you have taken during your academic career at the University of Waterloo (UW). It allows you to add, modify, and remove course information, providing a convenient way to manage your grades. Additionally, the application offers various visualizations, filtering, and sorting capabilities to give you a quick overview of your academic progress.

## Key Features
The application consists of three main sections: the course administration section, the visualization section, and the status bar.

#### Course Administration
The course administration section is divided into two parts: adding a course and listing all added courses.        To add a course, the user enters the course code, selects the term in which the course was taken, enters the received grade (within the range [0, ...       , 100] or WD), and clicks a button to create the new course.        The added courses are displayed in a list, allowing the user to edit or delete them.

#### Status Bar
The status bar, located at the bottom of the application, provides the following information:

Average of all non-WD'ed courses

Count of all taken courses

Count of all failed courses

Count of all WD'ed courses

#### Visualization Section
The visualization section includes four tabs, each presenting a different visualization:

**Average by Term:** Displays the average grade for each term in a Line & Marker chart. The x-axis represents the terms, ranging from the oldest to the newest, even if no courses were recorded for certain terms. The y-axis ranges from 0 to 100, with gridlines distinguishing each row. A marker indicates the average grade for each term.

**Progress towards Degree:** Groups all courses into three categories (CS, MATH, and Other) and displays the count of passed courses in each category as a horizontal bar graph. It also shows the remaining number of courses required to complete the degree. The visualization highlights the difference between completed and remaining courses using visual cues.

**Course Outcomes:** Categorizes courses into different groups based on their outcomes (WD'ed, failed, low, good, great, excellent, and missing) and presents the counts in a pie chart.   Hovering over a segment displays the courses contributing to that category. The pie chart can represent all added courses or all 40 courses required for the degree.

**Incremental Averages:** Similar to the "Average by Term" visualization, but each column represents the average of all courses taken up to and including that term. Unfilled circles indicate the marks for each course, and error bars represent the corrected standard deviation.

## Getting Started
To set up the Mark Management Application, follow these steps:

Install macOS 12.6 or a compatible operating system.

Install IntelliJ IDEA 2022.3.1 (Ultimate Edition) as your preferred Integrated Development Environment (IDE).

Ensure that you have kotlin.jvm 1.6.21 and Java SDK 17.0.5 (temurin) installed on your system.