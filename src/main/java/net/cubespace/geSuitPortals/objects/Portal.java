
package net.cubespace.geSuitPortals.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;


public class Portal {
	private String name;
	private String type;
	private String dest;
	private ArrayList<Loc> blocks = new ArrayList<>();
	private FillType fillType;
	private World world;
	private Location max;
	private Location min;

	public Portal(String name, String type, String dest, String fillType, Location max, Location min) {
		this.name = name;
		this.type = type;
		this.dest = dest;
		this.world = max.getWorld();
		this.max = max;
		this.min = min;
		try{
		this.fillType = FillType.valueOf(fillType.toUpperCase());
		}catch(Exception e){
			this.fillType = FillType.AIR;
			System.out.println("Invalid fill type for the portal, setting to AIR");
		}
		if(this.fillType==null){
			this.fillType = FillType.AIR;
		}
		for(int x = min.getBlockX(); x<=max.getBlockX(); x++){
			for(int y = min.getBlockY(); y<=max.getBlockY(); y++){
				for(int z = min.getBlockZ(); z<=max.getBlockZ(); z++){
					blocks.add(new Loc(max.getWorld().getName(), x, y, z));
				}
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public String getDestination(){
		return dest;
	}
	
	public FillType getFillType(){
		return fillType;
	}
	
	public Location getMax(){
		return max;
	}
	
	public Location getMin(){
		return min;
	}
	
	public void fillPortal(){
		for(Loc locs: blocks){
			Block b = locs.getBlock();
			if(b.isEmpty()){
				b.setType(fillType.getBlockMaterial());
			}
		}
	}
	
	public void clearPortal(){
		for(Loc locs: blocks){
			Block b = locs.getBlock();
			if(b.getType()==fillType.getBlockMaterial()){
				b.setType(fillType.AIR.getBlockMaterial());
			}else if(fillType.equals(FillType.LAVA) && b.getType().equals(Material.LAVA)){
				b.setType(fillType.AIR.getBlockMaterial());
			}else if(fillType.equals(FillType.WATER) && b.getType().equals(Material.WATER)){
				b.setType(fillType.AIR.getBlockMaterial());
			}
				
			
		}
	}
	
	public ArrayList<Loc> getBlocks(){
		return blocks;
	}
	
	public boolean isBlockInPortal(Block b){
		for(Loc l: blocks){
			if(l.equals(b)){
				return true;
			}
		}
		return false;
	}
	
	public World getWorld(){
		return world;
	}
	
	public boolean isLocationInPortal(Location l){
		for(Loc loc: blocks){
			if(loc.equals(l)){
				return true;
			}
		}
		return false;
	}

}