use std::collections::HashMap;
use std::fs;

fn main() {
    let file_content = fs::read_to_string("src/input.txt").expect("Unable to read file");
    let lines: Vec<&str> = file_content.split("\n").collect();
    println!("part one: {}", part_one(&lines));
    println!("part two: {}", part_two(lines));
}

fn part_one(lines: &Vec<&str>) -> u32 {
    let mut sum = 0;
    let limits: HashMap<&str, u32> = HashMap::from([("red", 12), ("green", 13), ("blue", 14)]);
    for (i, line) in lines.iter().enumerate() {
        let mut possible = true;
        'game: for set in get_sets(line) {
            let cubes: Vec<_> = set.split(", ").collect();
            for cube in cubes {
                let (color, amount) = get_cube(cube);
                if limits.get(color).unwrap().lt(&amount) {
                    possible = false;
                    break 'game;
                }
            }
        }
        if possible {
            sum += (i + 1) as u32;
        }
    }
    return sum;
}

fn part_two(lines: Vec<&str>) -> u32 {
    let mut sum = 0;
    for line in lines.iter() {
        let mut least: HashMap<&str, u32> = HashMap::from([("red", 1), ("green", 1), ("blue", 1)]);
        for set in get_sets(line) {
            for cube in get_cubes(set) {
                let (color, amount) = get_cube(cube);
                if let Some(curr) = least.get_mut(color) {
                    if amount > *curr {
                        *curr = amount;
                    }
                }
            }
        }
        let power = least.into_values().reduce(|acc, e| acc * e).unwrap();
        sum += power;
    }
    return sum;
}

fn get_sets(game: &str) -> Vec<&str> {
    return game.split(": ").skip(1).next().unwrap().split("; ").collect();
}

fn get_cubes(set: &str) -> Vec<&str> {
    return set.split(", ").collect();
}

fn get_cube(cube: &str) -> (&str, u32) {
    let color_amount: Vec<_> = cube.split(" ").collect();
    return (color_amount.get(1).unwrap(), color_amount.get(0).unwrap().parse::<u32>().unwrap());
}