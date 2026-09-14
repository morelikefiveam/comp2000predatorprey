# TO DO LIST 

## Predator
 
- Fill in movement, eat and reproduce stubs
- Write prey chase / eat logic
- Alter speed based on hunger (UNSURE)
- Decide how predator causes Prey to go into inDanger

## Prey
 
- Write logic for when being chased / in danger - *Oscar*
- Add a proximity check to eat() as prey currently eats from anywhere (need rendering done for it to work) - *Oscar* **DONE**
- Figure out how to not get stuck in an infite loop of chasing until starvation (going out of frame to rest or reproduce?) 

## Grass
 
- Add a grass tick loop (need rendering finished) - *Oscar*
- Figure out if and how grass will grow outwards if not eaten 

## Rendering / Simulation Loop
 
- **Build the jpanel simulation (MOST IMPORTANT)**
- Constrain creature movement (possibly) and grass spawns to frame bounds
- Skip dead creatures in loop
- Set up timer
