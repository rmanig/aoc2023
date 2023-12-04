use std::fs;

fn main() {
    let file_content = fs::read_to_string("src/input.txt").expect("Unable to read file");
    let lines: Vec<&str> = file_content.split("\n").collect();
    println!("part one: {}", part_one(&lines));
    println!("part two: {}", part_two(lines));
}

fn part_one(lines: &Vec<&str>) -> u32 {
    let mut sum = 0;
    for line in lines {
        sum += digit_sum(line);
    }
    return sum;
}

fn part_two(lines: Vec<&str>) -> u32 {
    let digits = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"];
    let mut sum = 0;
    for mut line in lines {
        let mut tmp;
        for (i, digit) in digits.iter().enumerate() {
            let mut to: String = digit.to_string();
            to.push_str(&((i + 1).to_string()));
            to.push_str(digit);
            tmp = line.replace(digit, to.as_str());
            line = &tmp;
        }
        sum += digit_sum(line);
    }
    return sum;
}

fn digit_sum(line: &str) -> u32 {
    let digits: Vec<u32> = line.chars()
        .filter_map(|s| s.to_digit(10))
        .collect();
    return digits[0] * 10 + digits[digits.len() - 1];
}