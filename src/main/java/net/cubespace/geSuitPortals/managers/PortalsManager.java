package net.cubespace.geSuitPortals.managers;

import net.cubespace.geSuitPortals.geSuitPortals;
import net.cubespace.geSuitPortals.objects.Portal;
import net.cubespace.geSuitPortals.tasks.PluginMessageTask;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PortalsManager {

    public static boolean RECEIVED = false;
    public static HashMap<World, ArrayList<Portal>> PORTALS = new HashMap<>();
    public static HashMap<String, Location> pendingTeleports = new HashMap<>();
    
    public static HashMap<World, HashMap<Long, ArrayList<Portal>>> PORTALE = new HashMap<>();
    public static HashMap<String, Portal> PORTALNAMES = new HashMap<>();
    
    public static long packXZIntoLong(int x, int z) {
        return ((long)x & 0xffffffff) | ((long)z & 0xffffffff) << 32;
    }
    
    public static void deletePortal( String name, String string ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "DeletePortal" );
            out.writeUTF( name );
            out.writeUTF( string );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );

    }

    public static void removePortal( String name ) {
        Portal p = getPortal( name );
        System.out.println( "removing portal " + name );
        if ( p != null ) {
            for (int z = p.getMin().getBlockZ() >> 4; z <= p.getMax().getBlockZ() >> 4; z++) {
                for (int x = p.getMin().getBlockX() >> 4; x <= p.getMax().getBlockZ() >> 4; x++) {
                    long chk = packXZIntoLong( x , z );
                    PORTALE.get( p.getMin().getWorld() ).get( chk ).remove( p );
                }
            }
            p.clearPortal();
        }
    }

    public static Portal getPortal( String name ) {
        return PORTALNAMES.get(name);
    }

    public static void getPortalsList( String name ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "ListPortals" );
            out.writeUTF( name );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );

    }

    public static void teleportPlayer( Player p, Portal portal ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TeleportPlayer" );
            out.writeUTF( p.getName() );
            out.writeUTF( portal.getType() );
            out.writeUTF( portal.getDestination() );
            out.writeBoolean( p.hasPermission( "gesuit.portals.portal." + portal.getName() ) || p.hasPermission( "gesuit.portals.portal.*" ) );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );
    }

    public static void setPortal( CommandSender sender, String name, String type, String dest, String fill ) {

        Player p = ( Player ) sender;
        Selection sel = geSuitPortals.WORLDEDIT.getSelection( p );

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetPortal" );
            out.writeUTF( sender.getName() );
            if ( sel == null || !( sel instanceof CuboidSelection ) ) {
                out.writeBoolean( false );
            } else {
                out.writeBoolean( true );
                out.writeUTF( name );
                out.writeUTF( type );
                out.writeUTF( dest );
                out.writeUTF( fill );
                Location max = sel.getMaximumPoint();
                Location min = sel.getMinimumPoint();
                out.writeUTF( max.getWorld().getName() );
                out.writeDouble( max.getX() );
                out.writeDouble( max.getY() );
                out.writeDouble( max.getZ() );
                out.writeUTF( min.getWorld().getName() );
                out.writeDouble( min.getX() );
                out.writeDouble( min.getY() );
                out.writeDouble( min.getZ() );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );

    }

        public static void addPortal( String name, String type, String dest, String filltype, Location max, Location min ) {
        if ( max.getWorld() == null ) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.RED + "World does not exist portal " + name + " will not load :(" );
            return;
        }
        Portal portal = new Portal( name, type, dest, filltype, max, min );
        PORTALNAMES.put(name, portal);
        for (int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++) {
            for (int x = min.getBlockX() >> 4; x <= max.getBlockZ() >> 4; x++) {
                long chk = packXZIntoLong( x , z );
        
                HashMap<Long, ArrayList<Portal>> wld = PORTALE.get( max.getWorld() );
                if ( wld == null ) {
                    wld = new HashMap<>();
                    PORTALE.put( max.getWorld(), wld );
                }
                ArrayList<Portal> chkl = wld.get( chk );
                if ( chkl == null ) {
                    chkl = new ArrayList<>();
                    wld.put( chk , chkl );
                }
                chkl.add( portal );
            }
        }
        portal.fillPortal();
    }

    public static void requestPortals() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "RequestPortals" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );

    }

    public static void sendVersion() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendVersion" );
            out.writeUTF( ChatColor.RED + "Portals - " + ChatColor.GOLD + geSuitPortals.INSTANCE.getDescription().getVersion() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( geSuitPortals.INSTANCE );
    }
}
