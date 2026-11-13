import zlib
import struct
import os

def write_png(filename, width, height, pixels):
    raw_data = bytearray()
    for row in pixels:
        raw_data.append(0)  # filter type 0 (None)
        for r, g, b, a in row:
            raw_data.extend([r, g, b, a])
    
    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)

    png = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    png += chunk(b'IHDR', ihdr)
    png += chunk(b'IDAT', zlib.compress(bytes(raw_data), level=9))
    png += chunk(b'IEND', b'')

    with open(filename, 'wb') as f:
        f.write(png)

def hex_to_rgba(hex_str, alpha=255):
    hex_str = hex_str.lstrip('#')
    if len(hex_str) == 6:
        r, g, b = int(hex_str[0:2], 16), int(hex_str[2:4], 16), int(hex_str[4:6], 16)
        return (r, g, b, alpha)
    elif len(hex_str) == 8:
        r, g, b, a = int(hex_str[0:2], 16), int(hex_str[2:4], 16), int(hex_str[4:6], 16), int(hex_str[6:8], 16)
        return (r, g, b, a)
    return (0, 0, 0, 0)

# Colors Palette matching user images
CLR = {
    'trans': (0, 0, 0, 0),
    'skin': hex_to_rgba('#F6C59E'),
    'skin_shadow': hex_to_rgba('#D79A6D'),
    'dark_hair': hex_to_rgba('#2A1F1D'),
    'brown_hair': hex_to_rgba('#4A2E1B'),
    'orange_hair': hex_to_rgba('#C25227'),
    'white_bone': hex_to_rgba('#F3EFE0'),
    'bone_shadow': hex_to_rgba('#C7BEA2'),
    'stone_light': hex_to_rgba('#9AA0A6'),
    'stone_mid': hex_to_rgba('#5F6368'),
    'stone_dark': hex_to_rgba('#3C4043'),
    'wood_light': hex_to_rgba('#A66E38'),
    'wood_mid': hex_to_rgba('#78471D'),
    'wood_dark': hex_to_rgba('#4A2810'),
    'straw_light': hex_to_rgba('#ECC057'),
    'straw_mid': hex_to_rgba('#C5942B'),
    'straw_dark': hex_to_rgba('#8E6519'),
    'leaf_green': hex_to_rgba('#489436'),
    'leaf_dark': hex_to_rgba('#27611A'),
    'red_banner': hex_to_rgba('#9C2525'),
    'red_berry': hex_to_rgba('#D32F2F'),
    'purple_berry': hex_to_rgba('#7B1FA2'),
    'fire_yellow': hex_to_rgba('#FFF176'),
    'fire_orange': hex_to_rgba('#FB8C00'),
    'fire_red': hex_to_rgba('#E53935'),
    'grass_bg': hex_to_rgba('#2A6F32'),
    'grass_light': hex_to_rgba('#3A8F43'),
    'grass_dark': hex_to_rgba('#1C5224'),
    'dirt_path': hex_to_rgba('#9E7746'),
    'pelt_orange': hex_to_rgba('#D97706'),
    'pelt_spot': hex_to_rgba('#451A03')
}

# 1. CREATE ICON FOR STONE AXE HUNTER (64x64)
def create_stone_axe_unit_icon():
    w, h = 64, 64
    grid = [[CLR['trans'] for _ in range(w)] for _ in range(h)]
    scale = 2
    
    # Hunter pixel art matrix (32x32)
    art = [
        "                                ",
        "            ........            ",
        "          ..########..          ",
        "         .############.         ",
        "        .####..##..####.        ",
        "        .####SS##SS####.        ",
        "        .###SSSSSSSS###.   GG   ",
        "         .##SSBSSBSS##.   GGGG  ",
        "         .##SSSSSSSS##.  GGGGGG ",
        "         ..#S#DDDD#S#.. GGGGGGG ",
        "          .##DDDDDD##.   WWWW   ",
        "         ..SSSSSSSSSS..  WW     ",
        "        .SS.PPPPPPPP.SS. WW     ",
        "       .SSSSPPPPPPPPSSSS.WW     ",
        "       .SSSSPOPPPPOPPSSS.WW     ",
        "       .SSS.PPPPPPPP.SSS.WW     ",
        "        .S.  PPPPPP  .S. WW     ",
        "        ..   SS  SS   .. WW     ",
        "             SS  SS      WW     ",
        "             LL  LL      WW     ",
        "            LLL  LLL            ",
        "                                ",
    ]
    # Mapping
    cmap = {
        ' ': CLR['trans'],
        '.': CLR['stone_dark'],
        '#': CLR['dark_hair'],
        'S': CLR['skin'],
        'B': hex_to_rgba('#1A1A1A'),
        'D': CLR['brown_hair'],
        'P': CLR['pelt_orange'],
        'O': CLR['pelt_spot'],
        'G': CLR['stone_light'],
        'W': CLR['wood_mid'],
        'L': CLR['wood_dark']
    }
    
    # Draw scaled to center
    for r_idx, row in enumerate(art):
        for c_idx, ch in enumerate(row):
            col = cmap.get(ch, CLR['trans'])
            if col != CLR['trans']:
                for dr in range(scale):
                    for dc in range(scale):
                        pr = 8 + r_idx * scale + dr
                        pc = c_idx * scale + dc
                        if 0 <= pr < h and 0 <= pc < w:
                            grid[pr][pc] = col
                            
    return grid

# 2. CREATE ICON FOR SPEAR GATHERER (64x64)
def create_spear_unit_icon():
    w, h = 64, 64
    grid = [[CLR['trans'] for _ in range(w)] for _ in range(h)]
    scale = 2
    
    art = [
        "                    SS          ",
        "          ...      SSSS         ",
        "        ..###..   SSSSSS        ",
        "        .#HHH#.    SSSS         ",
        "       .#HHHHH#.    WW          ",
        "       .##B#B##.    WW          ",
        "        .######.    WW          ",
        "        .SSBBSS.    WW          ",
        "       ..SSSSSS..   WW          ",
        "      .SS.EEEE.SS.  WW          ",
        "      .SSSEEEESSS.  WW          ",
        "      .SSSEEEESSS.  WW          ",
        "       .. EEEE ..   WW          ",
        "          EEEE      WW          ",
        "          SS SS     WW          ",
        "          SS SS     WW          ",
        "          LL LL     WW          ",
        "         LLL LLL    WW          ",
        "                                ",
    ]
    cmap = {
        ' ': CLR['trans'],
        '.': CLR['stone_dark'],
        '#': CLR['orange_hair'],
        'H': CLR['white_bone'],
        'S': CLR['skin'],
        'B': hex_to_rgba('#1A1A1A'),
        'E': CLR['leaf_green'],
        'G': CLR['stone_light'],
        'W': CLR['wood_mid'],
        'L': CLR['wood_dark']
    }
    for r_idx, row in enumerate(art):
        for c_idx, ch in enumerate(row):
            col = cmap.get(ch, CLR['trans'])
            if col != CLR['trans']:
                for dr in range(scale):
                    for dc in range(scale):
                        pr = 10 + r_idx * scale + dr
                        pc = c_idx * scale + dc
                        if 0 <= pr < h and 0 <= pc < w:
                            grid[pr][pc] = col
    return grid

# 3. CREATE CHIEFTAIN LONGHOUSE (TOWN HALL) ICON (96x96)
def create_chieftain_hall_icon():
    w, h = 96, 96
    grid = [[CLR['trans'] for _ in range(w)] for _ in range(h)]
    scale = 3
    
    art = [
        "              .B.               ",
        "             .BBB.              ",
        "           ..BWBW...            ",
        "          .WWWWWWWWW.           ",
        "         .SSSSSSSSSSS.          ",
        "       ..SSSSSSSSSSSSS..        ",
        "      .RR..SSSSSSSSS..RR.       ",
        "      .RR. .WWWWWWW. .RR.       ",
        "     .RRR. .WDDDDDW. .RRR.      ",
        "     .RBR. .WDDDDDW. .RBR.      ",
        "     .RRR. .WDDDDDW. .RRR.      ",
        "    .GGGGGGGGGGGGGGGGGGGGG.     ",
    ]
    cmap = {
        ' ': CLR['trans'],
        '.': CLR['stone_dark'],
        'B': CLR['white_bone'],
        'W': CLR['straw_dark'],
        'S': CLR['straw_light'],
        'R': CLR['red_banner'],
        'D': CLR['wood_dark'],
        'G': CLR['stone_mid']
    }
    for r_idx, row in enumerate(art):
        for c_idx, ch in enumerate(row):
            col = cmap.get(ch, CLR['trans'])
            if col != CLR['trans']:
                for dr in range(scale):
                    for dc in range(scale):
                        pr = 24 + r_idx * scale + dr
                        pc = c_idx * scale + dc
                        if 0 <= pr < h and 0 <= pc < w:
                            grid[pr][pc] = col
    return grid

# 4. CAMPFIRE WITH STONES ICON (64x64)
def create_campfire_icon():
    w, h = 64, 64
    grid = [[CLR['trans'] for _ in range(w)] for _ in range(h)]
    scale = 2
    
    art = [
        "              FY                ",
        "             FFYY               ",
        "            FFFOYY              ",
        "           FFFFOOYY             ",
        "          FFRROOOYYF            ",
        "          FRRRROOYFF            ",
        "           FRRRRROFF            ",
        "           ...WWW...            ",
        "         .GG.WWWWW.GG.          ",
        "        .GGGG..W..GGGG.         ",
        "        .GGGGG...GGGGG.         ",
        "         ..GGGGGGGGG..          ",
        "           .........            ",
    ]
    cmap = {
        ' ': CLR['trans'],
        '.': CLR['stone_dark'],
        'F': CLR['fire_red'],
        'O': CLR['fire_orange'],
        'Y': CLR['fire_yellow'],
        'W': CLR['wood_mid'],
        'G': CLR['stone_light']
    }
    for r_idx, row in enumerate(art):
        for c_idx, ch in enumerate(row):
            col = cmap.get(ch, CLR['trans'])
            if col != CLR['trans']:
                for dr in range(scale):
                    for dc in range(scale):
                        pr = 18 + r_idx * scale + dr
                        pc = 8 + c_idx * scale + dc
                        if 0 <= pr < h and 0 <= pc < w:
                            grid[pr][pc] = col
    return grid

out_dir = "app/src/main/res/drawable"
os.makedirs(out_dir, exist_ok=True)

write_png(f"{out_dir}/img_unit_stone_axe.png", 64, 64, create_stone_axe_unit_icon())
write_png(f"{out_dir}/img_unit_spear.png", 64, 64, create_spear_unit_icon())
write_png(f"{out_dir}/img_building_chief_hut.png", 96, 96, create_chieftain_hall_icon())
write_png(f"{out_dir}/img_campfire_stones.png", 64, 64, create_campfire_icon())

print("Created 4 PNG drawable assets successfully!")
