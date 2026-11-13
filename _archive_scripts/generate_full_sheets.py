import zlib
import struct
import os

def write_png(filename, width, height, pixels):
    raw_data = bytearray()
    for row in pixels:
        raw_data.append(0)
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
    return (int(hex_str[0:2], 16), int(hex_str[2:4], 16), int(hex_str[4:6], 16), alpha)

CLR = {
    'trans': (0, 0, 0, 0),
    'bg': hex_to_rgba('#0B1021'),
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
    'pelt_orange': hex_to_rgba('#D97706'),
    'pelt_spot': hex_to_rgba('#451A03')
}

# Generate 256x160 Troops Sheet
def generate_troops_sheet():
    w, h = 256, 160
    grid = [[CLR['bg'] for _ in range(w)] for _ in range(h)]
    
    # 6 Characters across x:
    # 0: Caveman, 1: Cavewoman, 2: Hunter (Stone Axe), 3: Gatherer (Spear), 4: Shaman (Torch), 5: Tribesman
    # Let's stamp them onto grid
    chars = [
        # (name, hair_col, weapon_type, dress_col)
        ("Caveman", CLR['dark_hair'], "none", CLR['pelt_orange']),
        ("Cavewoman", CLR['brown_hair'], "club", CLR['pelt_orange']),
        ("Hunter Axe", CLR['dark_hair'], "stone_axe", CLR['pelt_orange']),
        ("Gatherer Spear", CLR['orange_hair'], "spear", CLR['leaf_green']),
        ("Shaman Torch", CLR['white_bone'], "torch", CLR['pelt_spot']),
        ("Tribesman", CLR['dark_hair'], "bone", CLR['leaf_green'])
    ]
    
    for idx, (name, hair, wep, dress) in enumerate(chars):
        col_x = 16 + idx * 40
        # Draw Character in 2 poses (Idle top, Walk bottom)
        for pose_idx, y_start in enumerate([20, 85]):
            # Head
            for r in range(12):
                for c in range(12):
                    grid[y_start + r][col_x + c] = CLR['skin']
            # Hair
            for r in range(5):
                for c in range(14):
                    grid[y_start - 3 + r][col_x - 1 + c] = hair
            # Eyes
            grid[y_start + 5][col_x + 3] = (0, 0, 0, 255)
            grid[y_start + 5][col_x + 8] = (0, 0, 0, 255)
            # Body
            for r in range(14):
                for c in range(12):
                    grid[y_start + 12 + r][col_x + c] = dress
            # Legs
            leg_offset = 2 if pose_idx == 1 else 0
            for r in range(10):
                for c in range(4):
                    grid[y_start + 26 + r][col_x + 1 + c + leg_offset] = CLR['skin_shadow']
                    grid[y_start + 26 + r][col_x + 7 + c - leg_offset] = CLR['skin_shadow']
            
            # Weapon
            if wep == "stone_axe":
                for r in range(22):
                    grid[y_start + 8 + r][col_x + 15] = CLR['wood_mid']
                for r in range(8):
                    for c in range(8):
                        grid[y_start + 8 + r][col_x + 16 + c] = CLR['stone_light']
            elif wep == "spear":
                for r in range(32):
                    grid[y_start + r][col_x + 15] = CLR['wood_mid']
                for r in range(8):
                    for c in range(5):
                        grid[y_start - 4 + r][col_x + 13 + c] = CLR['stone_light']
            elif wep == "torch":
                for r in range(20):
                    grid[y_start + 10 + r][col_x + 15] = CLR['wood_mid']
                for r in range(7):
                    for c in range(7):
                        grid[y_start + 5 + r][col_x + 13 + c] = CLR['fire_orange']
                grid[y_start + 7][col_x + 15] = CLR['fire_yellow']
            elif wep == "club":
                for r in range(18):
                    for c in range(5):
                        grid[y_start + 10 + r][col_x + 14 + c] = CLR['wood_dark']

    return grid

out_dir = "app/src/main/res/drawable"
write_png(f"{out_dir}/img_prehistoric_troops.png", 256, 160, generate_troops_sheet())
print("Troop sheet generated successfully!")
